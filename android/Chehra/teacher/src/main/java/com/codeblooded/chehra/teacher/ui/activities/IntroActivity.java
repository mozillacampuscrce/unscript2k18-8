package com.codeblooded.chehra.teacher.ui.activities;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import com.codeblooded.chehra.teacher.BuildConfig;
import com.codeblooded.chehra.teacher.R;
import com.codeblooded.chehra.teacher.ui.fragments.DebugConfigFragment;

/**
 * Created by Samriddha on 04-01-2018.
 */

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description("Welcome")
                .image(R.drawable.teacher_foreground)
                .background(R.color.colorPrimary)
                .backgroundDark(R.color.colorPrimaryDark)
                .build());

        if (BuildConfig.DEBUG) {
            addSlide(new FragmentSlide.Builder()
                    .background(R.color.colorPrimary)
                    .backgroundDark(R.color.colorPrimaryDark)
                    .fragment(new DebugConfigFragment())
                    .build());
        }

    }
}
