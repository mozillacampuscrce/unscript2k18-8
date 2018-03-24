package com.codeblooded.chehra.teacher.ui.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.Lecture;
import com.codeblooded.chehra.teacher.ui.activities.CourseDetailActivity.ItemClickListener;

/**
 * Created by tejas on 7/1/18.
 */

public class LectureRecyclerViewAdapter extends RecyclerView.Adapter<LectureRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Lecture> lectures;
    private ItemClickListener listener;

    public LectureRecyclerViewAdapter(Context context, ArrayList<Lecture> lectures, ItemClickListener listener){
        this.context = context;
        this.lectures = lectures;
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView lect_num, lect_attendance;
        CardView parentView;
        ViewHolder(View view){
            super(view);
            lect_num = view.findViewById(R.id.lecture_num);
            parentView = view.findViewById(R.id.parentView);
            lect_attendance = view.findViewById(R.id.lect_attendance);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_lecture,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Lecture lecture = lectures.get(position);
        holder.lect_num.setText("Lecture "+String.valueOf(lecture.getLect_no()));
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(lecture);
            }
        });


        if(lecture.isAttendanceTaken())
            holder.lect_attendance.setText("Attendance:- Taken");
        else
            holder.lect_attendance.setText("Attendance:- Pending");
    }

    @Override
    public int getItemCount() {
        return lectures.size();
    }
}
