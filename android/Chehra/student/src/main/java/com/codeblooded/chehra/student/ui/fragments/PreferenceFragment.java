package com.codeblooded.chehra.student.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
//import com.codeblooded.chehra.student.ui.activities.FaceRecordActivity;
import com.codeblooded.chehra.student.ui.activities.IntroActivity;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

public class PreferenceFragment extends Fragment {
    SharedPreferences userPrefs;
    private OnPreferenceFragmentInteractionListener mListener;

    public PreferenceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference, container, false);

        TextView titleText = view.findViewById(R.id.toolbar_title);
        titleText.setText(R.string.preferences);

        userPrefs = view.getContext().getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);

        TextView name = view.findViewById(R.id.name);
        TextView email = view.findViewById(R.id.email);
        TextView rescan = view.findViewById(R.id.rescan_face_button);
        TextView intro = view.findViewById(R.id.replay_intro_button);
        TextView about = view.findViewById(R.id.about_button);
        TextView logout = view.findViewById(R.id.logout_button);

        String full_name = userPrefs.getString(Constants.FIRST_NAME, getString(R.string.student)) + " " + userPrefs.getString(Constants.LAST_NAME, "").trim();
        name.setText(full_name);
        email.setText(userPrefs.getString(Constants.EMAIL, getString(R.string.email)));

        rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), FaceRecordActivity.class));
            }
        });

        intro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), IntroActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LibsBuilder()
                        .withAboutAppName(getString(R.string.app_name))
                        .withAboutDescription(getString(R.string.app_description))
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .start(getActivity());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLogout();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPreferenceFragmentInteractionListener) {
            mListener = (OnPreferenceFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPreferenceFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnPreferenceFragmentInteractionListener {
        void onLogout();
    }

}
