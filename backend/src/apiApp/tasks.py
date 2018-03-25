from __future__ import absolute_import, unicode_literals
from celery import shared_task

from django.core.files.base import File
from rest_framework.response import Response
from django.core.files.storage import default_storage

from push_notifications.models import GCMDevice

from .models import StudentData, Student, Course, CourseData

from tempfile import TemporaryFile
import numpy as np

import pickle as pkl
from functools import reduce

import numpy as np
from sklearn.svm import SVC
import face_recognition as fc
import numpy as np
import cv2

#from .preprocessing import encode

#from ..chera import preprocessing, modelling
#from ..chera.preprocessing import generate_dataset
#from ..chera.modelling import predict,train


def train(student_ids, student_datas):
    """
    Given the list of student ids and student datas this will create a model
    and a mapping between the model predictions and the student_ids
    :param student_ids: List, list of student ids to be trained on
    :param student_datas: List, list of numpy arrays of a datafile
    corresponding to each element in student_ids
    :return: tuple, (model, mappings) where model is an sklearn model and
    mappings is list of student_ids where the index of the list corresponds to
    the model prediction. So mappings[model.predict()] will return the id of
    the student
    """

    arrays = [np.load(file_field.open("rb"))
              for file_field in student_datas]

    data_set = np.vstack(arrays)
    labels = reduce(lambda x, y: x + y,
                    [[i] * arrays[i].shape[0]
                     for i in range(len(student_ids))])

    clf = SVC(C=0.0001).fit(data_set, labels)

    return (clf, student_ids)


def predict(model, mappings, imgs):
    """
    Given the model, mappings and images of the classroom it returns list of
    student ids of those recognised and present
    :param model: Sklearn clf, classifier model for the course
    :param mappings: List, mappings for the course model predictions and the
    student ids
    :param imgs: imgs of the classroom
    :return: list, list of student_ids found present
    """
    final_preds = []

    for img in imgs:
        data = encode(img)
        preds = model.predict(data)
        final_preds = final_preds + preds

    final_preds = set(final_preds)

    student_ids = [mappings[i] for i in final_preds]

    return student_ids


def encode(img):
    """
    Given an image it returns the encodings for all the faces in the image
    :param img: Image or numpy array, the image whose encoding needs to be
    found
    :return: A numpy array of shape (n, 128) where n is the number of faces in
    detected in the image
    """

    face_locations = fc.face_locations(img, model="cnn")

    if face_locations:
        encodings = fc.face_encodings(img, face_locations)
        encodings = np.vstack(encodings)
    else:
        encodings = []

    return encodings


def generate_dataset(vid):
    """
    Given a video file path it finds the encodings of all the frames with a
    single face in the every frame. It returns a concatenated array which will
    be the training data set
    :param vid: String, full file path of the video
    :return: A numpy array of shape (n, 128) where n is the number of frames
    with a single face in it.
    """
    encodings = []
    video_capture = cv2.VideoCapture(vid)
    frames = []
    frame_count = 0
    batch_size = 4

    print("1")

    while video_capture.isOpened():
        ret, frame = video_capture.read()

        if not ret:
            break

        print("2")

        frame = frame[:, :, ::-1]
        (h, w) = frame.shape[:2]

        center = (w / 2, h / 2)
        m = cv2.getRotationMatrix2D(center, 90, 1.0)
        frame = cv2.warpAffine(frame, m, (w, h))

        print("3")

        frame_count += 1
        frames.append(frame)

        print("4")
        if len(frames) == batch_size:
            batch_of_face_locations = fc.batch_face_locations(frames)

            print("5")
            for frame_number_in_batch, face_locations in \
                    enumerate(batch_of_face_locations):
                if len(face_locations) == 1:

                    print("6")
                    top, right, bottom, left = face_locations[0]
                    image = frames[frame_number_in_batch]
                    face_image = image[top:bottom, left:right]
                    enc = fc.face_encodings(face_image,
                                            face_locations)[0]

                    print("7")
                    if len(enc):
                        encodings.append(enc)
            frames = []


    print("8")
    if encodings:
        encodings = np.vstack(encodings)
    else:
        encodings = []

    print("9")
    return encodings


@shared_task
def video_process(path, id):
    full_path = default_storage.path(path)

    print("10")
    dataset = generate_dataset(full_path)

    if len(dataset):
        outfile = TemporaryFile()
        np.save(outfile, dataset)

        print("11")
        student = Student.objects.get(student_id=id)
        f = File(outfile, '{0}.npy'.format(student))

        print("12")
        if StudentData.objects.filter(student_id=student).count() == 0:
            instance = StudentData(student_id=student, data=f)
        else:
            instance = StudentData.objects.filter(student_id=student).first()
            instance.data = f
        instance.save()

        print("13")
    else:

        print("14")
        student = Student.objects.get(student_id=id)
        user = student.user

        print("15")
        student_device = GCMDevice.objects.get(user=user)
        student_device.send_message("No face found in video, upload another one!")

    print("16")
    default_storage.delete(path)


@shared_task
def pics_process(imgs, lecture, teacher_user):
    course = lecture.course_id
    model, mappings = pkl.load(course.coursedata.data)

    student_ids = predict(model, mappings, imgs)
    absent_student_ids = list(set(mappings).difference(set(student_ids)))

    present_students = Student.objects.filter(
        student_id__in=student_ids).all()
    absent_students = Student.objects.filter(
        student_id__in=absent_student_ids).all()

    present_users = [st.user for st in present_students]
    absent_users = [st.user for st in absent_students]

    present_devices = GCMDevice.objects.filter(
        user__in=present_users)
    present_devices.send_message("You have been marked {0}"
                                 " for the lecture of {1} on {2}".format(
        'present', course.name, lecture.start_time))

    absent_devices = GCMDevice.objects.filter(
        user__in=absent_users)
    absent_devices.send_message("You have been marked {0}"
                                " for the lecture of {1} on {2}".format(
        'absent', course.name, lecture.start_time))

    teacher_device = GCMDevice.objects.get(user=teacher_user)
    teacher_device.send_message("Attendance has been marked!",
                                extra={
                                    "click_action": "",
                                    "isNotification": True,
                                    "lect_id": lecture.lect_id,
                                    "lect_no": lecture.lect_no
                                })


@shared_task
def course_process(course_id):
    course = Course.objects.get(course_id=course_id)
    students = course.students.all()
    # to add exception handling for no training data of students
    try:
        datas = [student.studentdata.data for student in students]
        ids = [student.student_id for student in students]

        saveable = train(ids, datas)

        outfile = TemporaryFile()
        pkl.dump(saveable, outfile)
        f = File(outfile, name='{0}.pkl'.format(course.course_id))

        if CourseData.objects.filter(course_id=course_id).count() == 0:
            course_data = CourseData(course_id=course, data=f)

        else:
            course_data = CourseData.objects.get(course_id=course)
            course_data.data = f

        course_data.save()

    except Exception as e:
        print(e)
