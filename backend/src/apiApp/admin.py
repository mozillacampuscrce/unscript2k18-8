from django.contrib import admin
from .models import (
    Teacher, Student,
    Department, Course,
    Lecture, StudentData, CourseData,
    Circular, ResultType, Result,
    Feedback
)
from django.contrib.auth import get_user_model
User = get_user_model()


# Register your models here.
class TeacherAdminModel(admin.ModelAdmin):
    # to display columns
    list_display = ["teacher_id", "user"]


class StudentAdminModel(admin.ModelAdmin):
    list_display = ["student_id", "user", "uid", "dept_id"]


class DepartmentAdminModel(admin.ModelAdmin):
    list_display = ["dept_id", "name"]


class CourseAdminModel(admin.ModelAdmin):
    list_display = ["course_id", "dept_id", "teacher_id",
                    "name", "description", "academic_yr", "year",
                    "updated", "created"]

    list_display_links = ["dept_id", "teacher_id"]


class CourseDataAdminModel(admin.ModelAdmin):
    list_display = ['data_id', 'course_id', 'data']


class LectureAdminModel(admin.ModelAdmin):
    list_display = ['lect_id', 'course_id', 'lect_no', 'isAttendanceTaken',
                    'start_time', 'end_time',
                    'comment', 'updated', 'created']


class StudentDataAdminModel(admin.ModelAdmin):
    list_display = ['data_id', 'student_id', 'data']


class UserAdminModel(admin.ModelAdmin):
    list_display = ['email', 'username', 'first_name', 'last_name']


class CircularAdminModel(admin.ModelAdmin):
    list_display = ['teacher_id', 'image_path']

    list_display_links = ['teacher_id']


class ResultTypeAdminModel(admin.ModelAdmin):
    list_display = ['res_type_id', 'name']


class ResultAdminModel(admin.ModelAdmin):
    list_display = ['result_id', 'course_id', 'result_type', 'xls_file', 'pdf_file']

    list_display_links = ['course_id', 'result_type']


class FeedbackAdminModel(admin.ModelAdmin):
    list_display = ['feedback_id', 'student_id', 'course_id', 'feedback']

    list_display_links = ['student_id', 'course_id']


admin.site.register(Teacher, TeacherAdminModel)
admin.site.register(Student, StudentAdminModel)
admin.site.register(Department, DepartmentAdminModel)
admin.site.register(Course, CourseAdminModel)
admin.site.register(CourseData, CourseDataAdminModel)
admin.site.register(Lecture, LectureAdminModel)
admin.site.register(StudentData, StudentDataAdminModel)
admin.site.register(User,UserAdminModel)
admin.site.register(Circular, CircularAdminModel)
admin.site.register(ResultType, ResultTypeAdminModel)
admin.site.register(Result, ResultAdminModel)
admin.site.register(Feedback, FeedbackAdminModel)