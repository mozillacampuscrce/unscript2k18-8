from django.conf.urls import url, include
from .views import index
from .auth.views import (
    Login, Register, StudentDataCreateAPIView,
    StudentDataUpdateAPIView, StudentDataGetListAPIView,
    StudentDataDeleteAPIView
)
from .department.views import (
    DepartmentListAPIView, DepartmentCreateAPIView,
    DepartmentDetailAPIView, DepartmentUpdateAPIView,
    DepartmentDeleteAPIView
)
from .course.views import (
    CourseCreateAPIView, CourseListAPIView,
    CourseDetailAPIView, CourseUpdateAPIView,
    CourseDeleteAPIView, CourseListByTeacherIdAPIView,
    CourseListByDeptIdAPIView, CourseDataCreateView,
    CourseEnrollmentRetrieveView, CourseReportDownloadView
)
from .lecture.views import (
    LectureCreateAPIView, LectureListByCourse,
    LectureUpdateAPIView, LectureAttendanceTakenView,
    LectureTakeAttendanceView
)
from .course_student.views import (
    EnrollInCourse, GetEnrolledStudentsByCourseIdListAPIView,
    GetEnrolledCoursesByStudentIdListAPIView,
    GetAvailableCoursesByStudentIdListAPIView,
)
from .lecture_student.views import (
    MarkAttendanceAPIView,StudentListByLectureIdListAPIView,
    LectureByStudentIdListAPIView,IsPresentForLectureDatesByCourseAPIView
)
from .classroom.views import ClassroomListAPIView

from .auth.views import GCDAuthViewSet

from django.conf import settings
from django.conf.urls.static import static

token_create_view = GCDAuthViewSet.as_view({
    'post': 'create'
})

urlpatterns = [
    url(r'^$', index),

    # auth
    url(r'^login/$', Login.as_view()),
    url(r'^register/$', Register.as_view()),

    url(r'^student/upload_data/$', StudentDataCreateAPIView.as_view()),
    url(r'^student/edit_data/$', StudentDataUpdateAPIView.as_view()),
    url(r'^student/get_datas/$', StudentDataGetListAPIView.as_view()),
    url(r'^student/delete_data/$', StudentDataDeleteAPIView.as_view()),


    # department
    url(r'^department/create/$', DepartmentCreateAPIView.as_view()),
    url(r'^department/get/$', DepartmentListAPIView.as_view()),
    url(r'^department/get/(?P<pk>\d+)/$', DepartmentDetailAPIView.as_view()),

        # put request eg: /api/department/update/2/ with params ['name']
    url(r'^department/update/(?P<pk>\d+)/$', DepartmentUpdateAPIView.as_view()),

        # delete request eg: /api/department/delete/2/
    url(r'^department/delete/(?P<pk>\d+)/$', DepartmentDeleteAPIView.as_view()),


    # course
    url(r'^course/create/$', CourseCreateAPIView.as_view()),
    url(r'^course/get/$', CourseListAPIView.as_view()),
    url(r'^course/get/(?P<pk>\d+)/$', CourseDetailAPIView.as_view()),
    url(r'^course/update/(?P<pk>\d+)/$', CourseUpdateAPIView.as_view()),
    url(r'^course/delete/(?P<pk>\d+)/$', CourseDeleteAPIView.as_view()),

    url(r'^course/getByTeacherId/$', CourseListByTeacherIdAPIView.as_view()),
    url(r'^course/getByDeptId/$', CourseListByDeptIdAPIView.as_view()),

    url(r'^course/create_data/$', CourseDataCreateView.as_view()),
    url(r'^course/checkEnrollment/$', CourseEnrollmentRetrieveView.as_view()),
    url(r'^lecture/checkAttendance/$', LectureAttendanceTakenView.as_view()),
    url(r'^lecture/takeAttendance/$', LectureTakeAttendanceView.as_view()),

    # lecture
    url(r'^lecture/create/$', LectureCreateAPIView.as_view()),

        # get request eg: /api/lecture/getByCourseId/?course_id=2
    url(r'^lecture/getByCourseId/$', LectureListByCourse.as_view()),

    url(r'^lecture/update/(?P<pk>\d+)/$', LectureUpdateAPIView.as_view()),


    # course-student
    url(r'^course/enrollStudentInCourse/$', EnrollInCourse.as_view()),   # course_id, student_id
    url(r'^course/getEnrolledStudents/$', GetEnrolledStudentsByCourseIdListAPIView.as_view()),
    url(r'^course/getEnrolledCourses/$', GetEnrolledCoursesByStudentIdListAPIView.as_view()),
    url(r'^course/getAvailableCourses/$', GetAvailableCoursesByStudentIdListAPIView.as_view()),
    url(r'^course/getReportUrl/$', CourseReportDownloadView.as_view()),


    # lecture-student
    url(r'^lecture/markAttendance/$', MarkAttendanceAPIView.as_view()),
    url(r'^lecture/getPresentStudents/$', StudentListByLectureIdListAPIView.as_view()),
    url(r'^lecture/getLecturesByStudentId', LectureByStudentIdListAPIView.as_view()),
    url(r'^lecture/getLecturesByStudentId/$', LectureByStudentIdListAPIView.as_view()),

    url(r'^token/create/$', token_create_view),
    url(r'^calendar/getIsPresentForLectureDatesByCourse/$', IsPresentForLectureDatesByCourseAPIView.as_view()),

    # classroom
    url(r'^classroom/get/$', ClassroomListAPIView.as_view()),

]
