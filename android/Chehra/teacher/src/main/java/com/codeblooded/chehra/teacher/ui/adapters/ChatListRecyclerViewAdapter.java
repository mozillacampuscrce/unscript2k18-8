package com.codeblooded.chehra.teacher.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.Chat;

import java.util.ArrayList;

/**
 * Created by Aashish Nehete on 24-Mar-18.
 */

public class ChatListRecyclerViewAdapter extends RecyclerView.Adapter<ChatListRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Chat> chats;

    public ChatListRecyclerViewAdapter(Context context, ArrayList<Chat> chats){
        this.chats = chats;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_chat,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        if(chat.getSender().toString().equals("teacher")){
            holder.responseTextView.setVisibility(View.GONE);
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.messageTextView.setText(chat.getMsg());
        }
        else{
            holder.responseTextView.setVisibility(View.VISIBLE);
            holder.messageTextView.setVisibility(View.GONE);
            holder.responseTextView.setText(chat.getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView,responseTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            responseTextView = itemView.findViewById(R.id.responseTextView);
        }


    }
}
