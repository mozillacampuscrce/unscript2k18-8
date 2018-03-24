package com.codeblooded.chehra.student.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.db.AttendanceDb;
import com.codeblooded.chehra.student.db.AttendanceDbHelper;
import com.codeblooded.chehra.student.models.BiMap;
import com.codeblooded.chehra.student.util.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity {

    SharedPreferences preferences, userPrefs;
    BiMap<Integer, String> departmentMap = new BiMap<>();
    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView, mUidView, mFirstnameView, mLastnameView, mConfirmPasswordView;
    private Spinner mDeptSpinner;
    private View mProgressView;
    private View mLoginFormView;
    private boolean register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences(Constants.APP_PREFS, MODE_PRIVATE);
        userPrefs = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (preferences.getBoolean(Constants.FIRST_START, true)) {
                    startActivity(new Intent(LoginActivity.this, IntroActivity.class));
                    preferences.edit().putBoolean(Constants.FIRST_START, false).apply();
                }
            }
        }).start();

        mLoginFormView = findViewById(R.id.login_form);
        setLayout(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDepartments();
    }

    private void fetchDepartments() {
        RestClient.get("department/get/", null, null, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                showProgress(false);
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject dept = response.getJSONObject(i);
                        departmentMap.put(dept.getInt(AttendanceDb.Department.COLUMN_NAME_DEPT_ID), dept.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                AttendanceDbHelper helper = new AttendanceDbHelper(LoginActivity.this);
                helper.putDepartments(departmentMap);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                showProgress(false);
                Toast.makeText(LoginActivity.this, "Error fetching initialization data", Toast.LENGTH_SHORT).show();
                Log.e(LoginActivity.this.getLocalClassName(), responseString);
            }
        });
    }

    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            RequestParams params = new RequestParams();
            params.put("email", mUsernameView.getText().toString().trim());
            params.put("password", mPasswordView.getText().toString().trim());
            params.put("isTeacher", false);
            RestClient.post("login/", null, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    showProgress(false);
                    Log.e("LoginActivity", response.toString());
                    SharedPreferences.Editor editor = preferences.edit();
                    SharedPreferences.Editor userEditor = userPrefs.edit();
                    editor.putBoolean(Constants.LOGGED_IN, true);
                    try {
                        userEditor.putString(Constants.TOKEN, response.getString(Constants.TOKEN));
                        userEditor.putBoolean(Constants.IS_VIDEO_ADDED, response.getBoolean(Constants.IS_VIDEO_ADDED));

                        JSONObject user = response.getJSONObject("user");
                        userEditor.putString(Constants.USERNAME, user.getString(Constants.USERNAME));
                        userEditor.putString(Constants.EMAIL, user.getString(Constants.EMAIL));
                        userEditor.putString(Constants.FIRST_NAME, user.getString(Constants.FIRST_NAME));
                        userEditor.putString(Constants.LAST_NAME, user.getString(Constants.LAST_NAME));

                        JSONObject student = response.getJSONObject("student");
                        userEditor.putInt(Constants.DEPT_ID, student.getInt(Constants.DEPT_ID));
                        userEditor.putInt(Constants.ID, student.getInt(Constants.ID));
                        userEditor.putString(Constants.UID, student.getString(Constants.UID));

                        Toast.makeText(LoginActivity.this, getString(R.string.welcome) + " " + user.getString(Constants.FIRST_NAME), Toast.LENGTH_SHORT).show();
                        userEditor.apply();
                        editor.apply();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    showProgress(false);
                    try {
                        Toast.makeText(LoginActivity.this, getString(R.string.login_error) + "\n(" + errorResponse.getString("error") + ")", Toast.LENGTH_SHORT).show();
                        Log.e(LoginActivity.this.getLocalClassName(), errorResponse.toString());
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, R.string.login_error, Toast.LENGTH_SHORT).show();
                    Log.e(LoginActivity.this.getLocalClassName(), responseString);
                }
            });
        }
    }

    private void attemptRegistration() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mUidView.setError(null);
        mFirstnameView.setError(null);
        mLastnameView.setError(null);
        mConfirmPasswordView.setError(null);
        ((TextView) mDeptSpinner.getSelectedView()).setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String uid = mUidView.getText().toString().trim();
        String firstname = mFirstnameView.getText().toString().trim();
        String lastname = mLastnameView.getText().toString().trim();
        String confirmPassword = mConfirmPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_field_required));
            focusView = mConfirmPasswordView;
            cancel = true;
        } else if (!confirmPassword.equals(password)) {
            mConfirmPasswordView.setError(getString(R.string.passwords_do_not_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (mDeptSpinner.getSelectedItemPosition() == 0) {
            ((TextView) mDeptSpinner.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = mDeptSpinner.getSelectedView();
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            RequestParams params = new RequestParams();
            params.put("username", username);
            params.put("password", password);
            params.put("isTeacher", false);
            params.put("uid", uid);
            params.put("dept_id", departmentMap.getKey(mDeptSpinner.getSelectedItem().toString()));
            params.put("email", username);
            params.put("first_name", firstname);
            params.put("last_name", lastname);
            RestClient.post("register/", null, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, R.string.post_registration, Toast.LENGTH_SHORT).show();
                    setLayout(false);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                    try {
                        Log.e(LoginActivity.this.getLocalClassName(), errorResponse.toString());
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                    Log.e(LoginActivity.this.getLocalClassName(), responseString);
                }
            });
        }
    }

    public void setLayout(boolean register) {
        ViewGroup mSceneRoot = findViewById(R.id.scene_root);
        Transition transition = new AutoTransition();

        if (register) {
            Scene RegisterScene = Scene.getSceneForLayout(mSceneRoot, R.layout.layout_register, this);
            this.register = true;
            TransitionManager.go(RegisterScene, transition);

            mUsernameView = findViewById(R.id.username);
            mPasswordView = findViewById(R.id.password);
            mFirstnameView = findViewById(R.id.firstname);
            mLastnameView = findViewById(R.id.lastname);
            mUidView = findViewById(R.id.uid);
            mConfirmPasswordView = findViewById(R.id.confirm_password);

            mDeptSpinner = findViewById(R.id.spinner);
            departmentMap.getMap().put(-1, "Department");
            String[] myItems = departmentMap.getMap().values().toArray(new String[departmentMap.getMap().values().size()]);
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
            mDeptSpinner.setAdapter(arrayAdapter);
            mDeptSpinner.setPrompt("Department");
            departmentMap.getMap().remove(-1);

            mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mRegisterButton = findViewById(R.id.email_register_button);
            mRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptRegistration();
                }
            });

        } else {
            Scene LoginScene = Scene.getSceneForLayout(mSceneRoot, R.layout.layout_login, this);
            this.register = false;
            TransitionManager.go(LoginScene, transition);

            mUsernameView = findViewById(R.id.username);
            mPasswordView = findViewById(R.id.password);

            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
            Button mEmailRegisterButton = findViewById(R.id.email_register_button);
            mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setLayout(true);
                }
            });
        }

        mProgressView = findViewById(R.id.login_progress);
        showProgress(false);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (register) {
            setLayout(false);
        } else
            super.onBackPressed();
    }
}

