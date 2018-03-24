package com.codeblooded.chehra.student.ui.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.codeblooded.chehra.student.Constants;
import com.codeblooded.chehra.student.R;
import com.codeblooded.chehra.student.models.BiMap;
import com.codeblooded.chehra.student.util.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.roomorama.caldroid.CaldroidFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;

public class CalendarFragment extends Fragment {
    View view;
    TextView total, present, missed;
    AppCompatSpinner spinner;
    CaldroidFragment caldroidFragment;
    List<Date> list = new ArrayList<>();
    BiMap<Integer, String> coursesMap = new BiMap<>();
    SimpleDateFormat format = new SimpleDateFormat("MMMM YYYY", Locale.getDefault());
    ProgressDialog progress;
    private OnCalendarFragmentInteractionListener mListener;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        TextView titleText = view.findViewById(R.id.toolbar_title);
        titleText.setText(R.string.calendar);

        spinner = view.findViewById(R.id.course_spinner);
        progress = new ProgressDialog(view.getContext());
        progress.setMessage(getString(R.string.please_wait));

        total = view.findViewById(R.id.total_count);
        present = view.findViewById(R.id.attended_count);
        missed = view.findViewById(R.id.missed_count);

        getEnrolledCourses();

        caldroidFragment = new CaldroidFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.container, caldroidFragment)
                .commit();

        return view;
    }

    public void getEnrolledCourses() {
        SharedPreferences userPrefs = view.getContext().getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
        final Header[] headers = new Header[]{new BasicHeader("Authorization", "JWT " + userPrefs.getString(Constants.TOKEN, ""))};

        final RequestParams params = new RequestParams();
        int dept_id = userPrefs.getInt(Constants.DEPT_ID, 0);
        params.put(Constants.DEPT_ID, dept_id);
        int student_id = userPrefs.getInt(Constants.ID, 0);
        params.put(Constants.ID, student_id);
        // TODO: Once backend sends year and academic_yr, use those values instead of dummy
        params.put(Constants.YEAR, 3);
        params.put(Constants.ACADEMIC_YR, 2017);
        RestClient.get("course/getEnrolledCourses/", headers, params, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                coursesMap.getMap().put(-1, getString(R.string.select_course));
                progress.show();
            }

            @Override
            public void onSuccess(int statusCode, final Header[] header, JSONArray response) {
                super.onSuccess(statusCode, header, response);
                progress.dismiss();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject item = response.getJSONObject(i);
                        coursesMap.put(item.getInt("course_id"), item.getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String[] myItems = coursesMap.getMap().values().toArray(new String[coursesMap.getMap().values().size()]);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),
                        android.R.layout.simple_dropdown_item_1line, myItems) {
                    @Override
                    public boolean isEnabled(int position) {
                        return position != 0;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                @NonNull ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };
                spinner.setAdapter(arrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) return;
                        int course_id = coursesMap.getKey(spinner.getItemAtPosition(position).toString());
                        caldroidFragment.clearBackgroundDrawableForDates(list);
                        list.clear();

                        params.put(Constants.COURSE_ID, course_id);
                        RestClient.get("calendar/getIsPresentForLectureDatesByCourse/", headers, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onStart() {
                                super.onStart();
                                progress.show();
                            }

                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                progress.dismiss();
                                if (response == null) return;
                                int presentCount = 0;

                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject item = response.getJSONObject(i);

                                        Date date;
                                        String dateString = item.getString(Constants.START_TIME);
                                        int year = Integer.parseInt(dateString.substring(0, 4));
                                        int month = Integer.parseInt(dateString.substring(5, 7));
                                        int day = Integer.parseInt(dateString.substring(8, 10));
                                        Calendar calendar = Calendar.getInstance();
                                        calendar.set(year, month - 1, day);
                                        date = calendar.getTime();
                                        Log.d("Calendar Date", date.toString());

                                        if (item.getBoolean("is_present")) {
                                            presentCount++;
                                            caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.GREEN), date);
                                            list.add(date);
                                        } else {
                                            caldroidFragment.setBackgroundDrawableForDate(new ColorDrawable(Color.RED), date);
                                            list.add(date);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                caldroidFragment.refreshView();
                                total.setText(Integer.toString(response.length()));
                                present.setText(Integer.toString(presentCount));
                                missed.setText(Integer.toString(response.length() - presentCount));

                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                progress.dismiss();
                                try {
                                    Log.e("CalendarFragment", errorResponse.toString());
                                    Toast.makeText(getActivity(), "Failed to fetch data\n(" + errorResponse.getString("detail") + ")", Toast.LENGTH_SHORT).show();
                                } catch (JSONException | NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                super.onFailure(statusCode, headers, responseString, throwable);
                                progress.dismiss();
                                Log.e("CalendarFragment", responseString);
                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinner.setPrompt(getString(R.string.select_course));
                coursesMap.getMap().remove(-1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progress.dismiss();
                try {
                    Log.e("CalendarFragment", errorResponse.toString());
                    Toast.makeText(getActivity(), "Failed to fetch courses\n(" + errorResponse.getString("detail") + ")", Toast.LENGTH_SHORT).show();
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCalendarFragmentInteractionListener) {
            mListener = (OnCalendarFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCalendarFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCalendarFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCalendarInteraction();
    }
}
