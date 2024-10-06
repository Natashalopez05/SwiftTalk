package com.example.swifttalk.logic.utils;

import android.annotation.SuppressLint;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
  public static String getDateWithTime(Date date) {
    @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    return timeFormat.format(date);
  }

  public static String getDate(Date date) {
    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    return dateFormat.format(date);
  }

  public static String getTimeInHour(Date date) {
    @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    return timeFormat.format(date);
  }

  public static long daysBetween(Timestamp timestamp) {
    Date today = new Date();
    Date pastDate = timestamp.toDate();
    long diffInMillis = today.getTime() - pastDate.getTime();
    return diffInMillis / (1000 * 60 * 60 * 24);
  }

  public static boolean isOlderThan(Timestamp timestamp, int days) {
    Date today = new Date();
    Date pastDate = timestamp.toDate();

    Calendar cal = Calendar.getInstance();
    cal.setTime(pastDate);
    cal.add(Calendar.DAY_OF_YEAR, days);

    return today.after(cal.getTime());
  }

}
