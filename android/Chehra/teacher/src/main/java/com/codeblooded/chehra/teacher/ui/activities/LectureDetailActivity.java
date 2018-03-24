package com.codeblooded.chehra.teacher.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import com.codeblooded.chehra.teacher.Constants;
import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.Lecture;
import com.codeblooded.chehra.teacher.models.Student;
import com.codeblooded.chehra.teacher.ui.adapters.StudentRecyclerViewAdapter;
import com.codeblooded.chehra.teacher.util.RestClient;

/**
 * Created by tejas on 8/1/18.
 */

public class LectureDetailActivity extends AppCompatActivity {

    private static final String LOG = "LectureDetail";
    boolean isLectureSpecific = true;
    private Lecture lecture;
    private ArrayList<Student> students;
    private ProgressBar progressBar;
    private RecyclerView recyclerview;
    private boolean isAttendanceTaken;

    /*This is to be set as false if this activity is called from notification*/
    private boolean isLectureDataPresent = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail);

        students = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        recyclerview = findViewById(R.id.student_list_recyclerview);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.getBoolean("isNotification")){
                isLectureDataPresent = false;
                isAttendanceTaken = true;
                lecture = new Lecture();
                lecture.setLect_id(bundle.getInt("lect_id"));
                lecture.setLect_no(bundle.getInt("lect_no"));
            }
            else{
                isLectureDataPresent = true;
                lecture = bundle.getParcelable(Constants.LECTURE);
                isAttendanceTaken = lecture.isAttendanceTaken();
            }

            setTitle("Lecture "+lecture.getLect_no());

        }

        findViewById(R.id.takeAttendanceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markAttendance();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isAttendanceTaken) {
            fetchEnrolledStudents();
            findViewById(R.id.takeAttendanceButtonCard).setVisibility(View.GONE);
        } else {
            findViewById(R.id.takeAttendanceButtonCard).setVisibility(View.VISIBLE);
        }

    }

    private void markAttendance(){
        RequestParams params = new RequestParams();
        params.put("lect_id", lecture.getLect_id());
        params.put("course_id", lecture.getCourse_id());

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RestClient.get("lecture/takeAttendance/",headers,params, new JsonHttpResponseHandler(){

            @Override
            public void onStart() {
                super.onStart();
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progressDialog.dismiss();
                Log.e(LOG,response.toString());
                Toast.makeText(LectureDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();
                Log.e(LOG,response.toString());
                Toast.makeText(LectureDetailActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
                Log.e(LOG,responseString);
                Toast.makeText(LectureDetailActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                progressDialog.dismiss();
                Log.e(LOG,errorResponse.toString());
                Toast.makeText(LectureDetailActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEnrolledStudents(){
        RequestParams params = new RequestParams();
        params.put("course_id",lecture.getCourse_id());

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RestClient.get("course/getEnrolledStudents/",
                headers, params,
                new JsonHttpResponseHandler(){
                    @Override
                    public void onStart() {
                        super.onStart();
                        students.clear();
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                        Log.e(LOG,response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject o = response.getJSONObject(i);
                                int student_id = o.getInt("student_id");
                                int dept_id = o.getInt("dept_id");
                                String uid = o.getString("uid");
                                JSONObject user = o.getJSONObject("user");
                                String username = user.getString("username");
                                String email = user.getString("email");
                                String first_name = user.getString("first_name");
                                String last_name = user.getString("last_name");

                                Student student = new Student(uid,username,first_name,last_name,
                                        email,student_id,dept_id);

                                students.add(student);
                            }
                            updateUI(students);
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                            Log.e(LOG,"Json parsing error");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        progressBar.setVisibility(View.GONE);
                        Log.e(LOG,responseString);
                    }

                });

    }

    private void updateUI(ArrayList<Student> students){
        TextView no_students_view = findViewById(R.id.no_students_view);
        if(students.size() != 0){
            no_students_view.setVisibility(View.GONE);
            recyclerview.setLayoutManager(new LinearLayoutManager(this));
            StudentRecyclerViewAdapter adapter = new StudentRecyclerViewAdapter(this,
                    students, isLectureSpecific, new OnListItemClickListener() {
                @Override
                public void onChangeAttendanceClick(Student student,boolean has_attended) {
                    changeAttendance(student, has_attended);
                    onResume();
                }
            });

            recyclerview.setAdapter(adapter);
        }
        else{
            no_students_view.setVisibility(View.VISIBLE);
        }

    }

    private void changeAttendance(Student student,boolean has_attended){
        RequestParams params = new RequestParams();
        params.put("lect_id",lecture.getLect_id());
        params.put("student_id",student.getStudent_id());
        params.put("has_attended",has_attended);

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RestClient.post("lecture/markAttendance/", headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressBar.setVisibility(View.GONE);
                Log.e(LOG,response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(LOG,errorResponse.toString());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(LOG,responseString);
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public interface OnListItemClickListener {
        void onChangeAttendanceClick(Student student, boolean has_attended);
    }
}
