package com.codeblooded.chehra.student.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.util.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

public class FaceRecordActivity extends AppCompatActivity {
    CameraView cameraView;
    FloatingActionButton fab;
    ProgressBar progress;
    ImageButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_record);

        cameraView = findViewById(R.id.camera);
        fab = findViewById(R.id.captureButton);
        progress = findViewById(R.id.progress);
        toggle = findViewById(R.id.toggle_cam);

        // if no front camera is available, switch to back camera and hide the toggle
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            cameraView.toggleFacing();
            toggle.setVisibility(View.GONE);
        }

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onVideoTaken(File video) {
                super.onVideoTaken(video);
                upload(video);
            }
        });

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress.setVisibility(View.VISIBLE);
                fab.setEnabled(false);
                fab.setBackgroundColor(Color.DKGRAY);
                File file = new File(getCacheDir().getPath() + File.pathSeparator + "face");
                cameraView.startCapturingVideo(file, 6000);
                CountDownTimer timer = new CountDownTimer(6000, 1000) {
                    @Override
                    public void onTick(long l) {
                        progress.setProgress((int) (l / 1000));
                    }

                    @Override
                    public void onFinish() {
                        progress.setVisibility(View.GONE);
                    }
                };
                timer.start();
            }
        });
    }

    private void upload(File video) {

        final SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
        RequestParams params = new RequestParams();
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        try {
            params.put(Constants.VIDEO, video);
            int student_id = userPrefs.getInt(Constants.ID, 0);
            params.put(Constants.ID, student_id);

            final ProgressDialog progressDialog = new ProgressDialog(FaceRecordActivity.this);
            progressDialog.setCancelable(false);

            RestClient.setTimeOut(30000);

            RestClient.post("student/upload_data/", headers, params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                    progressDialog.setMessage(getString(R.string.uploading));
                    progressDialog.show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressDialog.dismiss();
                    Log.d(getLocalClassName(), response.toString());
                    userPrefs.edit().putBoolean(Constants.IS_VIDEO_ADDED, true).apply();
                    startActivity(new Intent(FaceRecordActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    progressDialog.dismiss();
                    if (errorResponse != null)
                        Log.e("FaceRecordActivity", errorResponse.toString());
                    else Log.e("FaceRecordActivity", throwable.getMessage());
                    Toast.makeText(FaceRecordActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    fab.setEnabled(true);
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }
}
