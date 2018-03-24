package com.codeblooded.chehra.teacher.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.StudentChat;

import java.util.ArrayList;

/**
 * Created by Aashish Nehete on 24-Mar-18.
 */

public class StudentChatListAdapter extends RecyclerView.Adapter<StudentChatListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<StudentChat> studentChats;

    public StudentChatListAdapter(Context context, ArrayList<StudentChat> studentChats) {
        this.context = context;
        this.studentChats = studentChats;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_student_chat,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudentChat chat = studentChats.get(position);
        holder.nameTextView.setText(chat.getName());
    }

    @Override
    public int getItemCount() {
        return studentChats.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
        }
    }



}
