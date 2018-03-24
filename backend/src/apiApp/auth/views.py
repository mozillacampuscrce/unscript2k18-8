from rest_framework_jwt.settings import api_settings

from rest_framework.response import Response
from ..custom_user_backend import UserBackend
from django.contrib.auth import get_user_model
from rest_framework.views import APIView

from .serializers import UserSerializer, TeacherSerializer, StudentSerializer

from ..models import Teacher, Student, Department


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


User = get_user_model()


def get_jwt(user):
    jwt_payload_handler = api_settings.JWT_PAYLOAD_HANDLER
    jwt_encode_handler = api_settings.JWT_ENCODE_HANDLER

    payload = jwt_payload_handler(user)
    token = jwt_encode_handler(payload)
    return token


class Register(APIView):

    permission_classes = (AllowAny,)
    authentication_classes = []

    def post(self, request, *args, **kwargs):
        if not request.POST:
            return Response({'Error': "Please provide username/password", 'msg': 'failure'}, status=400)

        email = request.POST.get('email')
        username = request.POST.get('username')
        password = request.POST.get('password')
        first_name = request.POST.get('first_name')
        last_name = request.POST.get('last_name')
        is_teacher = request.POST.get('isTeacher')

        is_teacher = not (is_teacher == 'False' or is_teacher == 'false' or is_teacher == 0 or is_teacher == '0')

        try:
            user = User.objects.create(email=email, username=username,
                                       first_name=first_name, last_name=last_name)
            user.set_password(password)
            user.save()

            if is_teacher:
                instance = Teacher(user=user)
                instance.save()
            else:
                uid = request.POST.get('uid')
                dept_id = request.POST.get('dept_id')
                dept_id = int(dept_id)
                department = Department.objects.get(dept_id=dept_id)
                instance = Student(user=user, uid=uid)
                instance.dept_id = department
                instance.save()

            return Response({'msg': 'success'})
        except Exception as e:
            print("Exception caught")
            user = User.objects.get(username=username)
            if user:
                user.delete()

            return Response(
                {
                    'msg': 'failure',
                    'Error': e.__str__()
                }, status=400
            )


class Login(APIView):

    permission_classes = (AllowAny,)
    authentication_classes = []

    def post(self, request, *args, **kwargs):
        if not request.POST:
            return Response({'Error': "Please provide email/password", 'msg': 'failure'}, status=400)

        email = request.POST.get('email')
        password = request.POST.get('password')
        is_teacher = request.POST.get('isTeacher')

        is_teacher = not (is_teacher == 'False' or is_teacher == 'false' or is_teacher == 0 or is_teacher == '0')

        print(is_teacher)
        try:
            user, user_exists = UserBackend.authenticate(self=self, email=email, password=password)
            if (not user) and user_exists:
                return Response({'msg': 'failure', 'error': 'Wrong password'})
            elif (not user) and not user_exists:
                return Response({'msg': 'failure', 'error': 'User not found'})
            if is_teacher:
                teacher = Teacher.objects.filter(user=user).first()

            else:
                student = Student.objects.filter(user=user).first()

        except Exception as e:
            print(e)
            return Response({'msg': 'failure', 'error': e.__str__()}, status=401)

        token = get_jwt(user)
        if not is_teacher:
            return Response({'token': token,
                             'is_teacher': is_teacher,
                             'student': StudentSerializer(student).data,
                             'user': UserSerializer(user).data
                             }, status=200)
        return Response({'token': token,
                         'is_teacher': is_teacher,
                         'teacher': TeacherSerializer(teacher).data,
                         'user': UserSerializer(user).data
                         }, status=200)
