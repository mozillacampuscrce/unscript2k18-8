from django.http import JsonResponse
from rest_framework.response import Response
from rest_framework.generics import (
    ListAPIView,
    CreateAPIView,
    RetrieveAPIView,
    UpdateAPIView,
    DestroyAPIView
)
from rest_framework.permissions import (
    AllowAny,
    IsAuthenticated,
    IsAdminUser,
    IsAuthenticatedOrReadOnly
)
from rest_framework.views import APIView
from ..models import Student, Course
from ..auth.serializers import StudentSerializer, UserSerializer
from ..course.serializers import CourseDetailSerializer
from rest_framework_jwt.authentication import JSONWebTokenAuthentication

from ..permissions import IsTeacher, IsStudent


class GetStudentsByTeacherIdListAPIView(APIView):

    permission_classes = (IsAuthenticated, IsTeacher)
    authentication_classes = (JSONWebTokenAuthentication,)

    def get(self, format=None):
        teacher_id = self.request.GET['teacher_id']

        course_ids = Course.Objects.filter(teacher_id=teacher_id).values_list('teacher_id',
                                                                              flat=True)
        print(course_ids)

        students = Student.objects.filter(course__in=course_ids)
        serializer = StudentSerializer(students, many=True)
        return Response(serializer.data)
