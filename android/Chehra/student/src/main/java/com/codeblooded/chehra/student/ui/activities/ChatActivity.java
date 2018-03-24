package com.codeblooded.chehra.student.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.models.Chat;
import com.codeblooded.chehra.student.models.TeacherChat;
import com.codeblooded.chehra.student.ui.adapters.ChatListRecyclerViewAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    private ArrayList<Chat> chats;
    private RecyclerView chatMessages;
    private EditText messageEditText;
    private ImageButton sendButton;
    private ChatListRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatMessages = (RecyclerView) findViewById(R.id.messageListView);
        messageEditText = (EditText) findViewById(R.id.messageEditText);
        sendButton = (ImageButton) findViewById(R.id.sendButton);

        chats = new ArrayList<>();
        chatMessages.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatListRecyclerViewAdapter(this, chats);
        chatMessages.setAdapter(adapter);

        TeacherChat teacherChat = getIntent().getExtras().getParcelable(Constants.TEACHER_CHAT);

        SharedPreferences userPrefs = getSharedPreferences(Constants.USER_PREFS, MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(String.valueOf(userPrefs.getInt(Constants.ID, 0)))
                .child(teacherChat.getTeacherID());

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                chats.add(chat);
                adapter.notifyDataSetChanged();
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
