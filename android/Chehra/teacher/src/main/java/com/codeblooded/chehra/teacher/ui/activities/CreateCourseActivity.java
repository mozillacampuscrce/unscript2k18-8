package com.codeblooded.chehra.teacher.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import com.codeblooded.chehra.teacher.Constants;
import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.data.AttendanceDbHelper;
import com.codeblooded.chehra.teacher.models.BiMap;
import com.codeblooded.chehra.teacher.util.RestClient;

/**
 * Created by tejas on 5/1/18.
 */

public class CreateCourseActivity extends AppCompatActivity {

    private static final String LOG = "CreateCourseActivity";
    Spinner academic_yr_spinner, year_spinner, department_spinner;
    AutoCompleteTextView course_name_textview, course_desc_textview;
    BiMap<Integer, String> academic_yr_Map = new BiMap<>();
    BiMap<Integer, String> year_Map = new BiMap<>();
    BiMap<Integer, String> departments;
    LinearLayout container;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        progressBar = findViewById(R.id.progressBar);
        container = findViewById(R.id.container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setLayout();

        findViewById(R.id.create_course_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCourseCreation();
            }
        });
    }

    private void setLayout(){
        course_name_textview = findViewById(R.id.course_name);
        course_desc_textview = findViewById(R.id.course_desc);
        progressBar = findViewById(R.id.progress);


        /**/
        academic_yr_spinner = findViewById(R.id.academic_yr_spinner);

        academic_yr_Map.getMap().put(-1, "Academic year");
        academic_yr_Map.put(2017, "2017-18");
        academic_yr_Map.put(2018, "2018-19");

        String[] myItems = {"Academic year", "2017-18", "2018-19"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, myItems) {
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
        academic_yr_spinner.setAdapter(arrayAdapter);
        academic_yr_spinner.setPrompt("Academic year");
        academic_yr_Map.getMap().remove(-1);

        /**/
        year_spinner = findViewById(R.id.year_spinner);
        year_Map.getMap().put(-1, "Year");
        year_Map.put(1, "1st Year");
        year_Map.put(2, "2nd Year");
        year_Map.put(3, "3rd Year");
        year_Map.put(4, "4th Year");

        String[] myItems2 = {"Year", "1st Year", "2nd Year", "3rd Year", "4th Year"};
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, myItems2) {
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
        year_spinner.setAdapter(arrayAdapter2);
        year_spinner.setPrompt("Year");
        year_Map.getMap().remove(-1);

        /**/
        department_spinner = findViewById(R.id.department_spinner);
        departments = new AttendanceDbHelper(this).getDepartments(false);
        departments.getMap().put(-1, "Department");
        String[] myItems3 = departments.getMap().values().toArray(new String[departments.getMap().values().size()]);
        ArrayAdapter<String> arrayAdapter3 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, myItems3) {
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
        department_spinner.setAdapter(arrayAdapter3);
        department_spinner.setPrompt("Department");
        departments.getMap().remove(-1);
    }

    private void attemptCourseCreation() {
        course_name_textview.setError(null);
        course_desc_textview.setError(null);
        ((TextView) academic_yr_spinner.getSelectedView()).setError(null);
        ((TextView) year_spinner.getSelectedView()).setError(null);
        ((TextView) department_spinner.getSelectedView()).setError(null);


        boolean cancel = false;
        View focusView = null;

        String name = course_name_textview.getText().toString().trim();
        String desc = course_desc_textview.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            course_name_textview.setError(getString(R.string.error_field_required));
            focusView = course_name_textview;
            cancel = true;
        } else if(TextUtils.isEmpty(desc)){
            course_desc_textview.setError(getString(R.string.error_field_required));
            focusView = course_name_textview;
            cancel = true;
        }else if (academic_yr_spinner.getSelectedItemPosition() == 0) {
            ((TextView) academic_yr_spinner.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = academic_yr_spinner.getSelectedView();
            cancel = true;
        }else if (year_spinner.getSelectedItemPosition() == 0) {
            ((TextView) year_spinner.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = year_spinner.getSelectedView();
            cancel = true;
        }else if (department_spinner.getSelectedItemPosition() == 0) {
            ((TextView) department_spinner.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = department_spinner.getSelectedView();
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);

            SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
            Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

            RequestParams params = new RequestParams();

            params.put("name", name);
            params.put("description", desc);
            params.put("dept_id", departments.getKey(department_spinner.getSelectedItem().toString()));
            params.put("teacher_id", userPrefs.getInt(Constants.ID, 0));
            params.put("academic_yr", academic_yr_Map.getKey(academic_yr_spinner.getSelectedItem().toString()));
            params.put("year", year_Map.getKey(year_spinner.getSelectedItem().toString()));

            RestClient.post("course/create/", headers, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    showProgress(false);
                    Log.e(LOG,response.toString());
                    Toast.makeText(CreateCourseActivity.this,"Course created successfully",Toast.LENGTH_SHORT).show();
                    finish();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    showProgress(false);
                    Log.e(CreateCourseActivity.this.getLocalClassName(), errorResponse.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    showProgress(false);
                    Toast.makeText(CreateCourseActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    Log.e(CreateCourseActivity.this.getLocalClassName(), responseString);
                }
            });
        }
    }

    private void showProgress(final boolean show) {

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        container.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        container.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });

        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

}
