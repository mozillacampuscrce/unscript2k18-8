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

import com.codeblooded.chehra.teacher.Constants;
import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.StudentChat;
import com.codeblooded.chehra.teacher.ui.adapters.StudentChatListAdapter;
import com.codeblooded.chehra.teacher.util.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentChatListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TeacherChatListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView chatsList;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<StudentChat> studentChats;

    public static StudentChatListFragment newInstance() {
        // Required empty public constructor
        return new StudentChatListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);
        chatsList = rootView.findViewById(R.id.recycler_view_chat_list);
        emptyView = rootView.findViewById(R.id.emptyView);
        studentChats = new ArrayList<>();

        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getStudentsList();
            }
        });
        return rootView;
    }

    private void updateUI(ArrayList<StudentChat> studentChats){
        if(studentChats.size() != 0){
            emptyView.setVisibility(View.GONE);
            chatsList.setLayoutManager(new LinearLayoutManager(getActivity()));
            chatsList.setAdapter(new StudentChatListAdapter(getActivity(),studentChats));
        }
        else{
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    public void getStudentsList() {
        SharedPreferences userPrefs = getActivity().getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        //TODO: fetch properly
        RequestParams params = new RequestParams();
        params.put("teacher_id", userPrefs.getInt(Constants.ID, 0));
        RestClient.get("teacher/getStudents/", headers, params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                swipeRefreshLayout.setRefreshing(false);

                studentChats.clear();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        StudentChat studentChat = new StudentChat(
                                item.getString("student_id"),
                                item.getString("name")
                        );
                        studentChats.add(studentChat);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateUI(studentChats);
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
