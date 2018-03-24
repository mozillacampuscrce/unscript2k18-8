package com.codeblooded.chehra.teacher.ui.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.codeblooded.chehra.teacher.Constants;
import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.Course;
import com.codeblooded.chehra.teacher.ui.fragments.CourseFragment;
import com.codeblooded.chehra.teacher.ui.fragments.PreferenceFragment;
import com.codeblooded.chehra.teacher.ui.fragments.StudentChatListFragment;
import com.codeblooded.chehra.teacher.util.RestClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

public class MainActivity extends AppCompatActivity implements CourseFragment.OnListFragmentInteractionListener,
        PreferenceFragment.OnPreferenceFragmentInteractionListener,
        StudentChatListFragment.OnFragmentInteractionListener {
    private static final String LOG = "MainActivity";
    SharedPreferences preferences, userPrefs;
    BottomNavigationView bottomNavigationView;
    StudentChatListFragment studentChatListFragment;
    private boolean backPressFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(Constants.APP_PREFS, MODE_PRIVATE);
        userPrefs = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
        bottomNavigationView = findViewById(R.id.bottom_nav);

        if (preferences.getBoolean(Constants.DEBUG_SERVER_ENABLED, false)) {
            RestClient.setBaseUrl(preferences.getString(Constants.DEBUG_SERVER_URL, ""));
        }

        if (!preferences.getBoolean(Constants.LOGGED_IN, false)) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            final FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.frameLayout, CourseFragment.newInstance(1))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_nav_home:
                            fm.beginTransaction()
                                    .replace(R.id.frameLayout, CourseFragment.newInstance(1))
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .commit();
                            break;

                        case R.id.bottom_nav_chat:
                            studentChatListFragment = StudentChatListFragment.newInstance();
                            fm.beginTransaction()
                                    .replace(R.id.frameLayout, studentChatListFragment)
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .commit();

                        case R.id.bottom_nav_settings:
                            fm.beginTransaction()
                                    .replace(R.id.frameLayout, new PreferenceFragment())
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .commit();
                            break;
                    }
                    return true;
                }
            });
            sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String id = "backend_channel_id";
            CharSequence name = getString(R.string.backend_channel_name);
            String description = getString(R.string.backend_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;

            mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(getResources().getColor(R.color.colorPrimary));
            mChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void sendRegistrationToServer(String token) {
        SharedPreferences userPrefs = this.getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        String JWT = userPrefs.getString(Constants.TOKEN, "");
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + JWT)};

        RequestParams params = new RequestParams();
        params.put("registration_id", token);
        params.put("cloud_message_type", "FCM");
        params.put("active", true);
        Log.e(LOG, "sendRegistrationToServer :- " + token);
        RestClient.post("token/create/", headers, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(LOG, response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e(LOG, response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                if (errorResponse != null) Log.e(LOG, errorResponse.toString());
                Toast.makeText(MainActivity.this, "Failed to create token", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (errorResponse != null) Log.e(LOG, errorResponse.toString());
                Toast.makeText(MainActivity.this, "Failed to create token", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(MainActivity.this, "Failed to create token", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (fragment instanceof CourseFragment) {
            ((CourseFragment) fragment).getCourses();
        }
    }

    @Override
    public void onListFragmentInteraction(Course item) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.COURSE, item);
        Intent intent = new Intent(MainActivity.this, CourseDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /*Chat Listener*/
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLogout() {
        userPrefs.edit().clear().apply();
        preferences.edit().putBoolean(Constants.LOGGED_IN, false).apply();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressFlag) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, R.string.back_press_prompt, Toast.LENGTH_SHORT).show();
            backPressFlag = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backPressFlag = false;
                }
            }, 2000);
        }
    }
}
