package com.codeblooded.chehra.teacher.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codeblooded.chehra.teacher.Constants;
import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.BiMap;
import com.codeblooded.chehra.teacher.models.Course;
import com.codeblooded.chehra.teacher.models.Lecture;
import com.codeblooded.chehra.teacher.ui.adapters.LectureRecyclerViewAdapter;
import com.codeblooded.chehra.teacher.util.DateTimeUtil;
import com.codeblooded.chehra.teacher.util.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

public class CourseDetailActivity extends AppCompatActivity {

    private static final String LOG = "CourseDetailActivity";
    private Course course;
    private boolean isEnrollmentOn = true;
    private RelativeLayout enrollment_off_view;
    private TextView lectures_empty_view;
    private RecyclerView lecturesRecyclerView;
    private FloatingActionButton lect_add_fab;
    private ArrayList<Lecture> lectures;
    private LinearLayout bottomSheet;
    private TextView date_textview, start_time_textview, end_time_textview, enrollment_textview;
    private EditText comment_edittext;
    private Calendar startTime, endTime;
    private ProgressBar progressBar;
    private BottomSheetDialog bottomSheetDialog;
    private Button generateReportButton, changeEnrollmentButton;
    private Spinner classroomSpinner;
    private BiMap<Integer, String> classroomBiMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            course = bundle.getParcelable(Constants.COURSE);
        }

        lectures = new ArrayList<>();
        classroomBiMap = new BiMap<>();
        progressBar = findViewById(R.id.progressBar);
        ((TextView) findViewById(R.id.course_name)).setText(course.getName());
        ((TextView) findViewById(R.id.course_info)).setText(course.getInfoText(this));
        TextView descTextView = findViewById(R.id.course_desc);
        String desc = course.getDescription();
        if (desc.equals("")) descTextView.setVisibility(View.GONE);
        else descTextView.setText(desc);

        changeEnrollmentButton = findViewById(R.id.stop_enrollment);
        enrollment_textview = findViewById(R.id.enrollment_textview);
        enrollment_off_view = findViewById(R.id.enrollment_off_view);
        lectures_empty_view = findViewById(R.id.lectures_empty_view);
        lecturesRecyclerView = findViewById(R.id.lecturesRecyclerView);
        lect_add_fab = findViewById(R.id.lect_add_fab);
        bottomSheet = findViewById(R.id.bottom_sheet);
        generateReportButton = findViewById(R.id.generate_report_button);


        lect_add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showButtonSheetDialog();
            }
        });

        findViewById(R.id.enrolledStudentsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IS_LECTURE_SPECIFIC, false);
                bundle.putInt(Constants.ID, Integer.parseInt(course.getCourse_id()));
                Intent intent = new Intent(CourseDetailActivity.this, StudentListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateReport();
            }
        });

        findViewById(R.id.parentView).setVisibility(View.GONE);
        fetchClassrooms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkEnrollment();
    }

    /**
     * Num of lectures is needed before creating a new lecture.
     * So fab is made visible only after existing lectures are loaded.
     */
    private void createLectureFabVisibility(boolean show) {
        if (show) lect_add_fab.setVisibility(View.VISIBLE);
        else lect_add_fab.setVisibility(View.GONE);
    }

    private void showButtonSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_lecture_create, null);

        comment_edittext = view.findViewById(R.id.comment_edittext);
        classroomSpinner = view.findViewById(R.id.classroom_spinner);

        classroomBiMap.getMap().put(-1, "Classroom");
        String[] myItems = classroomBiMap.getMap().values().toArray(new String[classroomBiMap.getMap().values().size()]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, myItems) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };
        classroomSpinner.setAdapter(arrayAdapter);
        classroomSpinner.setPrompt("Classroom");
        classroomBiMap.getMap().remove(-1);

        date_textview = view.findViewById(R.id.date_textview);
        view.findViewById(R.id.date_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                int monthPlus1 = monthOfYear + 1;
                                String monthString = (monthPlus1 < 10) ? "0" + monthPlus1 : "" + monthPlus1;
                                String dayString = (dayOfMonth < 10) ? "0" + dayOfMonth : "" + dayOfMonth;

                                date_textview.setText(year + "-" + monthString + "-" + dayString);

                                if (startTime == null) startTime = Calendar.getInstance();
                                if (endTime == null) endTime = Calendar.getInstance();
                                startTime.set(year, monthOfYear, dayOfMonth);
                                endTime.set(year, monthOfYear, dayOfMonth);
                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(Calendar.getInstance());
                dpd.show(getFragmentManager(), "DatePickerDialog");
            }
        });

        start_time_textview = view.findViewById(R.id.start_time_textview);
        view.findViewById(R.id.start_time_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd1 = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                String hourString = (hourOfDay < 10) ? "0" + hourOfDay : "" + hourOfDay;
                                String minString = (minute < 10) ? "0" + minute : "" + minute;

                                if (startTime == null) startTime = Calendar.getInstance();
                                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                startTime.set(Calendar.MINUTE, minute);
                                start_time_textview.setText(hourString + ":" + minString);
                            }
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd1.show(getFragmentManager(), "TimePickerDialog1");

            }
        });


        end_time_textview = view.findViewById(R.id.end_time_textview);
        view.findViewById(R.id.end_time_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd2 = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                                String hourString = (hourOfDay < 10) ? "0" + hourOfDay : "" + hourOfDay;
                                String minString = (minute < 10) ? "0" + minute : "" + minute;

                                if (endTime == null) endTime = Calendar.getInstance();
                                endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                endTime.set(Calendar.MINUTE, minute);
                                end_time_textview.setText(hourString + ":" + minString);
                            }
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd2.show(getFragmentManager(), "TimePickerDialog1");
            }
        });

        view.findViewById(R.id.createLectureButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (startTime != null && endTime != null && classroomSpinner.getSelectedItemPosition() != 0)
                    createLecture(DateTimeUtil.getFormattedDateTime(startTime),
                            DateTimeUtil.getFormattedDateTime(endTime),
                            comment_edittext.getText().toString(),
                            lectures.size() + 1,
                            classroomBiMap.getKey(classroomSpinner.getSelectedItem().toString())
                    );
                else Toast.makeText(CourseDetailActivity.this,
                        "Please fill all fields", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(CourseDetailActivity.this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void createLecture(String startTime, String endTime, String comment, int lect_no, int classroom_id) {
        RequestParams params = new RequestParams();
        params.put("course_id", course.getCourse_id());
        params.put("start_time", startTime);
        params.put("end_time", endTime);
        params.put("comment", comment);
        params.put("lect_no", lect_no);
        params.put("classroom", classroom_id);

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RestClient.post("lecture/create/", headers, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(LOG, response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(LOG, response.toString());
                try {
                    int lect_no = response.getInt("lect_no");
                    Toast.makeText(CourseDetailActivity.this, "Lecture " + lect_no + " created", Toast.LENGTH_SHORT).show();
                    if (bottomSheetDialog != null)
                        bottomSheetDialog.cancel();
                    getLectures();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) Log.e(LOG, errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(LOG, responseString);
            }
        });

    }

    private void getLectures() {
        RequestParams params = new RequestParams();
        params.put("course_id", course.getCourse_id());

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RestClient.get("lecture/getByCourseId/", headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                lectures.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                progressBar.setVisibility(View.GONE);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject o = response.getJSONObject(i);
                        int course_id = o.getInt("course_id");
                        int lect_no = o.getInt("lect_no");
                        int lect_id = o.getInt("lect_id");
                        String start_time = o.getString("start_time");
                        String end_time = o.getString("end_time");
                        String comment = o.getString("comment");
                        Lecture lecture = new Lecture(start_time, end_time, lect_id, course_id, comment, lect_no);
                        lecture.setAttendanceTaken(o.getBoolean("isAttendanceTaken"));
                        lectures.add(lecture);
                    }
                    updateUI(lectures);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateUI(ArrayList<Lecture> lectures) {
        ((TextView) findViewById(R.id.lect_count)).setText(String.valueOf(lectures.size()));
        if (lectures.size() != 0) {
            generateReportButton.setVisibility(View.VISIBLE);
            lectures_empty_view.setVisibility(View.GONE);
            lecturesRecyclerView.setLayoutManager(new LinearLayoutManager(CourseDetailActivity.this));
            lecturesRecyclerView.setAdapter(new LectureRecyclerViewAdapter(CourseDetailActivity.this, lectures,
                    new ItemClickListener() {
                        @Override
                        public void onItemClick(Lecture lecture) {
                            Intent intent = new Intent(CourseDetailActivity.this, LectureDetailActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(Constants.LECTURE, lecture);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }));
        } else {
            generateReportButton.setVisibility(View.GONE);
            lectures_empty_view.setVisibility(View.VISIBLE);
        }
        createLectureFabVisibility(true);
    }

    private void checkEnrollment() {
        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RequestParams params = new RequestParams();
        params.put("course_id", course.getCourse_id());

        RestClient.get("course/checkEnrollment/", headers, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressBar.setVisibility(View.GONE);
                Log.e(LOG, response.toString());
                try {
                    isEnrollmentOn = !response.getBoolean("enrollment_complete");
                    Log.e(LOG, "isEnrollmentOn " + isEnrollmentOn);
                    updateLayout();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void stopEnrollment() {
        RequestParams params = new RequestParams();
        params.put("course_id", course.getCourse_id());
        params.put("stop_enrollment", true);

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RestClient.post("course/create_data/", headers, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(LOG, response.toString());
                try {
                    if (response.getString("msg").equals("success")) {
                        new AlertDialog.Builder(CourseDetailActivity.this)
                                .setMessage("Enrollment stopped")
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        onResume();
                                    }
                                })
                                .create().show();
                    } else {
                        Toast.makeText(CourseDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e(LOG, response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(LOG, "OnFailure JSONArray");
                if (errorResponse != null)
                    Log.e(LOG, "->" + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(LOG, "OnFailure JSONObject");
                if (errorResponse != null)
                    Log.e(LOG, "->" + errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(LOG, "OnFailure Throwable");
                if (responseString != null)
                    Log.e(LOG, "->" + responseString);
            }
        });


    }

    private void startEnrollment() {
        RequestParams params = new RequestParams();
        params.put("course_id", course.getCourse_id());
        params.put("stop_enrollment", false);

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RestClient.post("course/create_data/", headers, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(LOG, response.toString());
                try {
                    if (response.getString("msg").equals("success")) {
                        new AlertDialog.Builder(CourseDetailActivity.this)
                                .setMessage("Enrollment started")
                                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        onResume();
                                    }
                                })
                                .create().show();
                    } else {
                        Toast.makeText(CourseDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        Log.e(LOG, response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

    }

    private void generateReport() {
        final ProgressDialog progressDialog = new ProgressDialog(CourseDetailActivity.this);

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RequestParams params = new RequestParams();
        params.add("course_id", course.getCourse_id());

        RestClient.get("course/getReportUrl/", headers, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                progressDialog.setMessage(getString(R.string.please_wait));
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();
                if (response != null) {
                    try {
                        String base = RestClient.getBaseUrl();
                        String urlString = base.substring(0, base.length() - 5) + response.getString("url");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(urlString));
                        startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                progressDialog.dismiss();
                Log.e(LOG, responseString);
                Toast.makeText(CourseDetailActivity.this, "Error generating report", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLayout() {
        findViewById(R.id.parentView).setVisibility(View.VISIBLE);
        if (isEnrollmentOn) {
            enrollment_textview.setText(getString(R.string.enrollment_on_text));
            enrollment_off_view.setVisibility(View.GONE);
            lect_add_fab.setVisibility(View.GONE);

            changeEnrollmentButton.setText(getString(R.string.stop_enrollment));
            changeEnrollmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(CourseDetailActivity.this)
                            .setTitle(getString(R.string.confirm_stop_enrollment))
                            .setMessage(getString(R.string.action_can_be_undone))
                            .setPositiveButton(getString(R.string.stop_enrollment), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    stopEnrollment();

                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //do nothing
                                }
                            })
                            .create()
                            .show();

                }
            });
        } else {
            enrollment_textview.setText(getString(R.string.enrollment_off_text));
            enrollment_off_view.setVisibility(View.VISIBLE);
            lect_add_fab.setVisibility(View.VISIBLE);
            createLectureFabVisibility(false);

            changeEnrollmentButton.setText(getString(R.string.start_enrollment));
            changeEnrollmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(CourseDetailActivity.this)
                            .setTitle(getString(R.string.confirm_start_enrollment))
                            .setMessage(getString(R.string.action_can_be_undone))
                            .setPositiveButton(getString(R.string.start_enrollment), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startEnrollment();

                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //do nothing
                                }
                            })
                            .create()
                            .show();

                }
            });
            getLectures();
        }

    }

    private void fetchClassrooms() {
        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RestClient.get("classroom/get/", headers, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        int id = object.getInt("classroom_id");
                        String name = object.getString("classroom_name");
                        classroomBiMap.put(id, name);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(LOG, responseString);
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

    public interface ItemClickListener {
        void onItemClick(Lecture lecture);
    }

}
