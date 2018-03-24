package com.codeblooded.chehra.teacher.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.Student;
import com.codeblooded.chehra.teacher.ui.activities.LectureDetailActivity.OnListItemClickListener;

/**
 * Created by tejas on 6/1/18.
 */

public class StudentRecyclerViewAdapter extends RecyclerView.Adapter<StudentRecyclerViewAdapter.StudentViewHolder>{

    private Context context;
    private ArrayList<Student> students;
    private boolean changeAttendanceOption;
    private OnListItemClickListener listener;

    public StudentRecyclerViewAdapter(Context context, ArrayList<Student> students,
                                      boolean changeAttendanceOption,
                                      OnListItemClickListener listener) {
        this.context = context;
        this.students = students;
        this.changeAttendanceOption = changeAttendanceOption;
        this.listener = listener;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StudentViewHolder holder, int position) {

        final Student student = students.get(position);
        String full_name = student.getFirst_name() + " " + student.getLast_name();

        //holder.username.setText("Username: "+student.getUsername());
        holder.name.setText(full_name);
        holder.email.setText("Email: "+student.getEmail());
        holder.uid.setText("UID: " + student.getUid());

        holder.student_detail.setVisibility(View.GONE);

        if (changeAttendanceOption) {
            holder.changeAttendance.setVisibility(View.VISIBLE);
            holder.changeAttendance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null)
                        new AlertDialog.Builder(context)
                                .setTitle("Change Attendance")
                                .setPositiveButton("Mark Present", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        listener.onChangeAttendanceClick(student,true);
                                    }
                                })
                                .setNegativeButton("Mark Absent", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        listener.onChangeAttendanceClick(student,false);
                                    }
                                })
                                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //do nothing
                                    }
                                })
                                .create().show();
                }
            });
        } else
            holder.changeAttendance.setVisibility(View.GONE);

        holder.expandView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.student_detail.getVisibility() == View.VISIBLE) {
                    holder.student_detail.setVisibility(View.GONE);
                    holder.expandView.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_expand_more_black_24dp));
                } else {
                    holder.student_detail.setVisibility(View.VISIBLE);
                    holder.expandView.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.ic_expand_less_black_24dp));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {
        View student_detail;
        TextView name, username, email,uid, lect_attendance;
        ImageView expandView;
        Button changeAttendance;

        StudentViewHolder(View view) {
            super(view);
            student_detail = view.findViewById(R.id.student_detail);
            name = view.findViewById(R.id.name);
            username = view.findViewById(R.id.username);
            email = view.findViewById(R.id.email);
            uid = view.findViewById(R.id.uid);
            expandView = view.findViewById(R.id.expand_view);
            changeAttendance = view.findViewById(R.id.change_attendance);
        }
    }
}
