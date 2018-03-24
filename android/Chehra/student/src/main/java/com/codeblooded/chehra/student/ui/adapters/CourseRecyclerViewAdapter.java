package com.codeblooded.chehra.student.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.models.Course;
import com.codeblooded.chehra.student.ui.fragments.CourseListFragment;

import java.util.ArrayList;

public class CourseRecyclerViewAdapter extends RecyclerView.Adapter<CourseRecyclerViewAdapter.ViewHolder> {

    private final CourseListFragment.OnListFragmentInteractionListener mListener;
    private ArrayList<Course> mValues;
    private Context context;
    private boolean enrolled;

    public CourseRecyclerViewAdapter(Context context, ArrayList<Course> items, CourseListFragment.OnListFragmentInteractionListener listener, boolean enrolled) {
        mValues = items;
        mListener = listener;
        this.context = context;
        this.enrolled = enrolled;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getCourse_id());
        holder.mTitleView.setText(mValues.get(position).getName());
        if (!"".equals(mValues.get(position).getDescription())) {
            holder.mContentView.setText(mValues.get(position).getDescription());
            holder.mContentView.setVisibility(View.VISIBLE);
        } else holder.mContentView.setVisibility(View.GONE);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, enrolled);
                }
            }
        });
    }

    public void updateList(ArrayList<Course> list) {
        mValues = list;
        notifyDataSetChanged();
    }

    public ArrayList<Course> getItems() {
        return mValues;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        TextView mIdView, mTitleView, mContentView;
        Course mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mTitleView = view.findViewById(R.id.title);
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
