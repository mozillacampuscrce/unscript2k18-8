package com.codeblooded.chehra.teacher.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.models.StudentChat;
import com.codeblooded.chehra.teacher.ui.adapters.StudentChatListAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StudentChatListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class StudentChatListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView chatsList;
    private TextView emptyView;

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
