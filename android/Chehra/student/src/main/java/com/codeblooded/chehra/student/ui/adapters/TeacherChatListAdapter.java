package com.codeblooded.chehra.student.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.models.TeacherChat;
import com.codeblooded.chehra.student.ui.activities.ChatActivity;

import java.util.ArrayList;

/**
 * Created by Aashish Nehete on 24-Mar-18.
 */

public class TeacherChatListAdapter extends RecyclerView.Adapter<TeacherChatListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TeacherChat> studentChats;

    public TeacherChatListAdapter(Context context, ArrayList<TeacherChat> studentChats) {
        this.context = context;
        this.studentChats = studentChats;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_teacher_chat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TeacherChat chat = studentChats.get(position);
        holder.nameTextView.setText(chat.getName());

        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.CHAT, chat);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentChats.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        LinearLayout parentView;

        public ViewHolder(View itemView) {
            super(itemView);
            parentView = itemView.findViewById(R.id.parentView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }


}
