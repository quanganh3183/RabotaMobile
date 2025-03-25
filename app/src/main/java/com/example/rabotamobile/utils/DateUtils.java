package com.example.rabotamb.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";

    public static String formatDateForDisplay(String apiDate) {
        try {
            SimpleDateFormat apiFormat = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
            Date date = apiFormat.parse(apiDate);
            return date != null ? displayFormat.format(date) : "";
        } catch (ParseException e) {
            e.printStackTrace();
            return apiDate;
        }
    }

    public static String formatDateForApi(String displayDate) {
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat apiFormat = new SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault());
            Date date = displayFormat.parse(displayDate);
            return date != null ? apiFormat.format(date) : "";
        } catch (ParseException e) {
            e.printStackTrace();
            return displayDate;
        }
    }
}