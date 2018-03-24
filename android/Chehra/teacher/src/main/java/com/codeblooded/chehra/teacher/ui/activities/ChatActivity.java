package com.codeblooded.chehra.teacher.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.codeblooded.chehra.teacher.Constants;
import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.Chat;
import com.codeblooded.chehra.teacher.models.StudentChat;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    FrameLayout fragmentContainer;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        StudentChat studentChat = bundle.getParcelable(Constants.CHAT);

        fragmentContainer = (FrameLayout) findViewById(R.id.fragment_container_chat);

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference("chats")
                .child(String.valueOf(userPrefs.getInt(Constants.ID, 1)))
                .child(studentChat.getStudentID());

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);

                // TODO: Use chat to populate the view
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
