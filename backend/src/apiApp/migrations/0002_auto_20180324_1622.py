# Generated by Django 2.0 on 2018-03-24 16:22

import apiApp.models
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('apiApp', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Circular',
            fields=[
                ('circ_id', models.AutoField(primary_key=True, serialize=False)),
                ('image_path', models.FileField(upload_to=apiApp.models.circular_upload_location)),
                ('teacher_id', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='apiApp.Teacher')),
            ],
        ),
        migrations.CreateModel(
            name='Feedback',
            fields=[
                ('feedback_id', models.AutoField(primary_key=True, serialize=False)),
                ('feedback', models.CharField(max_length=255)),
                ('course_id', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='apiApp.Course')),
                ('student_id', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='apiApp.Student')),
            ],
        ),
        migrations.CreateModel(
            name='Lecture',
            fields=[
                ('lect_id', models.AutoField(primary_key=True, serialize=False)),
                ('lect_no', models.IntegerField(blank=True)),
                ('start_time', models.DateTimeField()),
                ('end_time', models.DateTimeField()),
                ('comment', models.CharField(blank=True, max_length=50)),
                ('updated', models.DateTimeField(auto_now=True)),
                ('created', models.DateTimeField(auto_now_add=True)),
                ('isAttendanceTaken', models.BooleanField(default=False)),
                ('course_id', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='apiApp.Course')),
                ('students', models.ManyToManyField(to='apiApp.Student')),
            ],
        ),
        migrations.CreateModel(
            name='Result',
            fields=[
                ('result_id', models.AutoField(primary_key=True, serialize=False)),
                ('xls_file', models.FileField(upload_to=apiApp.models.result_xls_upload_location)),
                ('pdf_file', models.FileField(upload_to=apiApp.models.result_pdf_upload_location)),
                ('course_id', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='apiApp.Course')),
            ],
        ),
        migrations.CreateModel(
            name='ResultType',
            fields=[
                ('res_type_id', models.AutoField(primary_key=True, serialize=False)),
                ('name', models.CharField(max_length=50)),
            ],
        ),
        migrations.CreateModel(
            name='StudentData',
            fields=[
                ('data_id', models.AutoField(primary_key=True, serialize=False)),
                ('data', models.FileField(upload_to=apiApp.models.training_upload_location)),
                ('student_id', models.OneToOneField(on_delete=django.db.models.deletion.CASCADE, to='apiApp.Student')),
            ],
        ),
        migrations.AddField(
            model_name='result',
            name='result_type',
            field=models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='apiApp.ResultType'),
        ),
    ]