package com.codeblooded.chehra.student.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.models.Course;
import com.codeblooded.chehra.student.util.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class CourseDetailActivity extends AppCompatActivity {

    SharedPreferences userPrefs;
    TextView total, present, missed;
    View statsCard;
    PieChartView pieChartView;
    private Course course;
    private boolean enrolled, isEnrollmentOn = true;
    private Button enrollButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);

        total = findViewById(R.id.total_count);
        present = findViewById(R.id.attended_count);
        missed = findViewById(R.id.missed_count);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            course = bundle.getParcelable(Constants.COURSE);
            enrolled = bundle.getBoolean(Constants.IS_ENROLLED);
        }

        ((TextView) findViewById(R.id.course_name)).setText(course.getName());
        ((TextView) findViewById(R.id.course_info)).setText(course.getInfoText(CourseDetailActivity.this));
        TextView descTextView = findViewById(R.id.course_desc);
        enrollButton = findViewById(R.id.enrollButton);
        String desc = course.getDescription();
        if (desc.equals("")) descTextView.setVisibility(View.GONE);
        else descTextView.setText(desc);

        statsCard = findViewById(R.id.stats_card);
        pieChartView = findViewById(R.id.pie);

        if (enrolled) {
            enrollButton.setText(R.string.enrolled);
            enrollButton.setEnabled(false);
            statsCard.setVisibility(View.VISIBLE);
            getAttendanceStats();
        } else {
            enrollButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

                    RequestParams params = new RequestParams();
                    int student_id = userPrefs.getInt(Constants.ID, 0);
                    params.put("course_id", course.getCourse_id());
                    params.put(Constants.ID, student_id);
                    RestClient.post("course/enrollStudentInCourse/", headers, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            enrollButton.setText(R.string.please_wait);
                            enrollButton.setEnabled(false);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            enrollButton.setText(R.string.enrolled);
                            Toast.makeText(CourseDetailActivity.this, R.string.enrolled, Toast.LENGTH_SHORT).show();
                            Log.e("Enrolment", response.toString());
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
                            enrollButton.setText(R.string.enrolled);
                            Toast.makeText(CourseDetailActivity.this, R.string.enrolled, Toast.LENGTH_SHORT).show();
                            Log.e("Enrolment", response.toString());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            enrollButton.setText(R.string.enroll);
                            enrollButton.setEnabled(true);
                            try {
                                Toast.makeText(CourseDetailActivity.this, "Failed to enroll\n(" + errorResponse.getString("detail") + ")", Toast.LENGTH_SHORT).show();
                                Log.e("Enrolment", errorResponse.toString());
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }

    }

    public void getAttendanceStats() {
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RequestParams params = new RequestParams();
        int student_id = userPrefs.getInt(Constants.ID, 0);
        params.put(Constants.ID, student_id);
        params.put(Constants.COURSE_ID, course.getCourse_id());
        RestClient.get("calendar/getIsPresentForLectureDatesByCourse/", headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                if (response == null) return;
                int presentCount = 0;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);

                        if (item.getBoolean("is_present")) {
                            presentCount++;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                int totalCount = response.length();
                int missedCount = totalCount - presentCount;
                List<SliceValue> values = new ArrayList<SliceValue>();
                SliceValue presentSlice = new SliceValue((float) presentCount / totalCount, getResources().getColor(R.color.green));
                values.add(presentSlice);
                SliceValue absentSlice = new SliceValue((float) missedCount / totalCount, getResources().getColor(R.color.red));
                values.add(absentSlice);

                PieChartData data = new PieChartData(values);
                pieChartView.setPieChartData(data);

                total.setText(Integer.toString(response.length()));
                present.setText(Integer.toString(presentCount));
                missed.setText(Integer.toString(missedCount));

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                try {
                    Log.e("CalendarFragment", errorResponse.toString());
                    Toast.makeText(CourseDetailActivity.this, "Failed to fetch data\n(" + errorResponse.getString("detail") + ")", Toast.LENGTH_SHORT).show();
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("CalendarFragment", responseString);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
}
