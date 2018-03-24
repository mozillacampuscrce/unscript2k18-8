package com.codeblooded.chehra.student.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.models.Course;
import com.codeblooded.chehra.student.ui.adapters.CourseRecyclerViewAdapter;

import java.util.ArrayList;

public class CoursesFragment extends Fragment {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    public CoursesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_courses, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager = view.findViewById(R.id.container);
        tabLayout = view.findViewById(R.id.tabs);
        final SearchView searchView = view.findViewById(R.id.searchView);

        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String newText = searchView.getQuery().toString();
                Fragment fragment = mSectionsPagerAdapter.getItem(position);
                if (fragment instanceof CourseListFragment) {
                    TextView emptyText = fragment.getView().findViewById(R.id.emptyText);
                    CourseListFragment f = (CourseListFragment) fragment;
                    CourseRecyclerViewAdapter adapter = (CourseRecyclerViewAdapter) f.getRecyclerView().getAdapter();
                    if (adapter == null) {
                        return;
                    }
                    if (!"".equals(newText)) {
                        ArrayList<Course> temp = new ArrayList<>();
                        for (Course c : f.getCourseList()) {
                            if (c.getName().toLowerCase().contains(newText.toLowerCase())
                                    || c.getDescription().toLowerCase().contains(newText.toLowerCase())) {
                                temp.add(c);
                            }
                        }
                        if (temp.isEmpty()) {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText(R.string.no_results);
                        } else {
                            emptyText.setVisibility(View.GONE);
                            emptyText.setText(R.string.no_courses);
                        }
                        adapter.updateList(temp);
                    } else {
                        // Show all results is search bar is empty
                        adapter.updateList(f.getCourseList());
                        if (f.getCourseList().isEmpty()) {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText(R.string.no_courses);
                        } else {
                            emptyText.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Fragment fragment = mSectionsPagerAdapter.getItem(mViewPager.getCurrentItem());
                if (fragment instanceof CourseListFragment) {
                    TextView emptyText = fragment.getView().findViewById(R.id.emptyText);
                    CourseListFragment f = (CourseListFragment) fragment;
                    CourseRecyclerViewAdapter adapter = (CourseRecyclerViewAdapter) f.getRecyclerView().getAdapter();
                    if (adapter == null) {
                        return false;
                    }
                    if (!"".equals(newText)) {
                        ArrayList<Course> temp = new ArrayList<>();
                        for (Course c : f.getCourseList()) {
                            if (c.getName().toLowerCase().contains(newText.toLowerCase())
                                    || c.getDescription().toLowerCase().contains(newText.toLowerCase())) {
                                temp.add(c);
                            }
                        }
                        if (temp.isEmpty()) {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText(R.string.no_results);
                        } else {
                            emptyText.setVisibility(View.GONE);
                            emptyText.setText(R.string.no_courses);
                        }
                        adapter.updateList(temp);
                    } else {
                        // Show all results is search bar is empty
                        adapter.updateList(f.getCourseList());
                        if (f.getCourseList().isEmpty()) {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText(R.string.no_courses);
                        } else {
                            emptyText.setVisibility(View.GONE);
                        }
                    }
                }
                return false;
            }
        });

        return view;
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
        CourseListFragment fragment1 = CourseListFragment.newInstance(1, true);
        CourseListFragment fragment2 = CourseListFragment.newInstance(1, false);

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fragment1;
                default:
                    return fragment2;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
