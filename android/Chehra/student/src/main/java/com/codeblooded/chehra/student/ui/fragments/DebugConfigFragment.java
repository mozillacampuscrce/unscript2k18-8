package com.codeblooded.chehra.student.ui.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.util.RestClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugConfigFragment extends Fragment {


    public DebugConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_debug_config, container, false);
        final SharedPreferences.Editor editor = view.getContext().getSharedPreferences(Constants.APP_PREFS, Context.MODE_PRIVATE).edit();

        final TextInputEditText hostname_editText = view.findViewById(R.id.hostname_editText);
        Button save = view.findViewById(R.id.hostname_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = hostname_editText.getText().toString().trim();
                if (!"".equals(url) && Patterns.WEB_URL.matcher(url).matches()) {
                    if (!url.startsWith("http"))
                        url = "http://" + url;
                    if (!url.endsWith("/"))
                        url = url + "/";
                    if (!url.endsWith("api/"))
                        url = url + "api/";
                    RestClient.setBaseUrl(url);
                    Toast.makeText(view.getContext(), "BASE_URL : " + url, Toast.LENGTH_SHORT).show();
                    editor.putBoolean(Constants.DEBUG_SERVER_ENABLED, true);
                    editor.putString(Constants.DEBUG_SERVER_URL, url);
                    editor.apply();
                    hostname_editText.clearFocus();
                } else {
                    Toast.makeText(view.getContext(), getString(R.string.invalid), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
