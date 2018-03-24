package com.codeblooded.chehra.student.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.models.TeacherChat;
import com.codeblooded.chehra.student.ui.adapters.TeacherChatListAdapter;
import com.codeblooded.chehra.student.util.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

public class TeacherChatListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView chatsList;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<TeacherChat> teacherChats;

    public static TeacherChatListFragment newInstance() {
        // Required empty public constructor
        return new TeacherChatListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);
        chatsList = rootView.findViewById(R.id.recycler_view_chat_list);
        emptyView = rootView.findViewById(R.id.emptyView);
        teacherChats = new ArrayList<>();

        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTeachersList();
            }
        });
        return rootView;
    }

    private void updateUI(ArrayList<TeacherChat> teacherChats) {
        if (teacherChats.size() != 0) {
            emptyView.setVisibility(View.GONE);
            chatsList.setLayoutManager(new LinearLayoutManager(getActivity()));
            chatsList.setAdapter(new TeacherChatListAdapter(getActivity(), teacherChats));
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    public void getTeachersList() {
        SharedPreferences userPrefs = getActivity().getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        //TODO: fetch properly
        RequestParams params = new RequestParams();
        params.put("student_id", userPrefs.getInt(Constants.ID, 0));
        RestClient.get("student/getTeachers/", headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                swipeRefreshLayout.setRefreshing(false);

                teacherChats.clear();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        TeacherChat teacherChat = new TeacherChat(
                                item.getString("teacher_id"),
                                item.getString("name")
                        );
                        teacherChats.add(teacherChat);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateUI(teacherChats);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeRefreshLayout.setRefreshing(false);
                try {
                    Toast.makeText(getActivity(), "Failed to fetch courses\n(" + errorResponse.getString("detail") + ")", Toast.LENGTH_SHORT).show();
                    Log.e("CourseFragment", errorResponse.toString());
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
