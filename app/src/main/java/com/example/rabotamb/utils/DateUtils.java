package com.example.rabotamb.utils;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String TAG = "DateUtils";

    public static String formatDateForApi(String displayDate) {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000'Z'", Locale.getDefault());
            Date date = displayFormat.parse(displayDate);
            return apiFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date", e);
            return "";
        }
    }

    public static String formatDateForDisplay(String apiDate) {
        try {
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = apiFormat.parse(apiDate);
            return displayFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date", e);
            return "";
        }
    }
}