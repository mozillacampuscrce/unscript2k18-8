package com.codeblooded.chehra.student.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(String.valueOf(userPrefs.getInt(Constants.ID, 0)));
    }
}
