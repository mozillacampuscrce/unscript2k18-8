package com.codeblooded.chehra.student.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.models.Course;
import com.codeblooded.chehra.student.ui.adapters.CourseRecyclerViewAdapter;
import com.codeblooded.chehra.student.util.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

public class CourseListFragment extends Fragment {

    public static final String LOG = "CourseListFragment";
    private static final String ARG_COURSE_TYPE = "course_type";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private boolean mEnrolled;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ArrayList<Course> courses = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    public CourseListFragment() {
    }

    public static CourseListFragment newInstance(int columnCount, boolean enrolled) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putBoolean(ARG_COURSE_TYPE, enrolled);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mEnrolled = getArguments().getBoolean(ARG_COURSE_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCourses();
            }
        });

        getCourses();

        return view;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public ArrayList<Course> getCourseList() {
        return courses;
    }

    public void getCourses() {
        SharedPreferences userPrefs = view.getContext().getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        RequestParams params = new RequestParams();
        int dept_id = userPrefs.getInt(Constants.DEPT_ID, 0);
        params.put(Constants.DEPT_ID, dept_id);
        int student_id = userPrefs.getInt(Constants.ID, 0);
        params.put(Constants.ID, student_id);
        // TODO: Dummy values
        params.put(Constants.YEAR, 3);
        params.put(Constants.ACADEMIC_YR, 2017);
        String url = (mEnrolled) ? "course/getEnrolledCourses/" : "course/getAvailableCourses/";
        RestClient.get(url, headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                courses.clear();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        Course course = new Course(item.getString("course_id"),
                                item.getString("dept_id"),
                                item.getString("teacher_id"),
                                item.getString("name"),
                                item.getString("description"),
                                item.getString("academic_yr"),
                                item.getString("year"),
                                item.getString("updated"),
                                item.getString("created"));
                        courses.add(course);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (!courses.isEmpty()) {
                    Context context = view.getContext();
                    if (mColumnCount <= 1) {
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                    } else {
                        recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                    }
                    recyclerView.setAdapter(new CourseRecyclerViewAdapter(getActivity(), courses, mListener, mEnrolled));

                    view.findViewById(R.id.emptyText).setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.emptyText).setVisibility(View.VISIBLE);
                    recyclerView.removeAllViews();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeRefreshLayout.setRefreshing(false);
                try {
                    Toast.makeText(getActivity(), "Failed to fetch courses\n(" + errorResponse.getString("detail") + ")", Toast.LENGTH_SHORT).show();
                    Log.e("CourseListFragment", errorResponse.toString());
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Course item, boolean enrolled);
    }
}
