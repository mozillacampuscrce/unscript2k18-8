package com.codeblooded.chehra.teacher.util;

import android.util.Log;

import java.util.Calendar;

public class DateTimeUtil {

    private static final String LOG = "DateTimeUtil";

    //format:- 2019-01-09T18:00
    public static String getFormattedDateTime(Calendar calendar) {
        if (calendar == null) return null;
        String date = getFormattedDate(calendar);
        String time = getFormattedTime(calendar);
        if (date == null || time == null) return null;
        else return date + "T" + time;
    }

    // format:- 2019-01-09
    public static String getFormattedDate(Calendar calendar) {
        try {
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int year = calendar.get(Calendar.YEAR);

            String monthString = (month < 10) ? "0" + month : "" + month;
            String dayString = (day < 10) ? "0" + day : "" + day;

            String date = year + "-" + monthString + "-" + dayString;
            Log.e(LOG, date);
            return date;
        } catch (Exception e) {
            return null;
        }
    }

    //format:- 18:00
    public static String getFormattedTime(Calendar calendar) {
        try {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            String hourString = (hour < 10) ? "0" + hour : "" + hour;
            String minString = (min < 10) ? "0" + min : "" + min;
            return hourString + ":" + minString;
        } catch (Exception e) {
            return null;
        }
    }

}
