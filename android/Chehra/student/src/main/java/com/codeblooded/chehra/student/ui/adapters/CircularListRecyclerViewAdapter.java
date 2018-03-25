package com.codeblooded.chehra.student.ui.adapters;

/**
 * Created by kaustubh on 25-03-2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.codeblooded.chehra.student.R;

import java.util.ArrayList;

public class CircularListRecyclerViewAdapter extends RecyclerView.Adapter<CircularListRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LinearLayout> circulars;

    public CircularListRecyclerViewAdapter(Context context, ArrayList<LinearLayout> circulars){
        this.context = context;
        this.circulars = circulars;
    }

    @Override
    public CircularListRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_chat,parent,false);

        return new CircularListRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView circularTitle;
        /*db query to retreive circular title*/
        String title = "";
        holder.circularTitle.setText(title);
    }

    @Override
    public int getItemCount() {
        return circulars.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout circular;
        TextView circularTitle;
        public ViewHolder(View itemView) {
            super(itemView);
            circularTitle = itemView.findViewById(R.id.circularTitle);
            circular = itemView.findViewById(R.id.circularView);

        }


    }
}
