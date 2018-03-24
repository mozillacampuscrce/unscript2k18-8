package com.codeblooded.chehra.teacher.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.codeblooded.chehra.teacher.R;

public class ChatActivity extends AppCompatActivity {

    FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container_chat);


    }
}
