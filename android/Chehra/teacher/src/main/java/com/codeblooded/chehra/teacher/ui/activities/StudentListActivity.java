package com.codeblooded.chehra.teacher.ui.activities;

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
import com.codeblooded.chehra.teacher.models.Student;
import com.codeblooded.chehra.teacher.ui.adapters.StudentRecyclerViewAdapter;
import com.codeblooded.chehra.teacher.util.RestClient;

/**
 * Created by tejas on 6/1/18.
 */

public class StudentListActivity extends AppCompatActivity {

    RecyclerView recyclerview;
    boolean isLectureSpecific = false;
    ArrayList<Student> students = new ArrayList<>();
    int id;
    public static final String LOG = "StudentListActivity";
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        recyclerview = findViewById(R.id.student_list_recyclerview);
        progressBar = findViewById(R.id.progressBar);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            isLectureSpecific = bundle.getBoolean(Constants.IS_LECTURE_SPECIFIC);
            id = bundle.getInt(Constants.ID);
        }

        if(isLectureSpecific){
            fetchAttendedStudents();
        }
        else{
            fetchEnrolledStudents();
        }

    }

    private void updateUI(ArrayList<Student> students){
        TextView no_students_view = findViewById(R.id.no_students_view);
        if(students.size() != 0){
            no_students_view.setVisibility(View.GONE);
            recyclerview.setLayoutManager(new LinearLayoutManager(this));
            StudentRecyclerViewAdapter adapter = new StudentRecyclerViewAdapter(this,
                    students, isLectureSpecific, null);

            recyclerview.setAdapter(adapter);
        }
        else{
            no_students_view.setVisibility(View.VISIBLE);
        }

    }

    private void fetchEnrolledStudents(){
        RequestParams params = new RequestParams();
        params.put("course_id",id);

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

    //TODO
    private void fetchAttendedStudents(){

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
}
