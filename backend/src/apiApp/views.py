from django.shortcuts import render

# Create your views here.
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render
from .models import Lecture, Student, Course,Teacher, Circular,Result

# Create your views here.
@csrf_exempt
def index(request):
    return JsonResponse({'text': 'hello This is index page',
                         'status': '200 OK'}
                        )


def markAttendance(lect_id, student_id, has_attended):

    lecture = Lecture.objects.filter(lect_id=lect_id).first()
    student = Student.objects.filter(student_id=student_id).first()

    if has_attended:
        lecture.students.add(student)
    else:
        lecture.students.remove(student)


def isStudentEnrolledInCourse(student_id, course):
    # student = Student.objects.filter(student_id=student_id)
    course = Course.objects.filter(course_id=course.course_id).first()
    queryset = course.students.filter(student_id=student_id)
    print(queryset)
    return queryset.count() == 1

@csrf_exempt
def showlogin(request):
    context ={
        "title":"Tej"
    }
    return render(request,"index.html",context)


@csrf_exempt
def showWork(request):
    teacher_id = request.GET['teacher_id']
    #teacher = Teacher.objects.filter(user=self.request.user).first()
    print(teacher_id)
    queryset = Course.objects.filter(teacher_id=teacher_id)

    circulars = Circular.objects.filter(teacher_id=teacher_id)

    results = Result.objects.all()



    print(queryset)
    context ={
        "queryset": queryset,
        "circulars": circulars
    }
    return render(request, "work.html",context)


