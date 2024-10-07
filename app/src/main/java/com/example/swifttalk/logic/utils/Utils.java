package com.example.swifttalk.logic.utils;

import android.annotation.SuppressLint;
import com.example.swifttalk.logic.models.Messages.MessageType;
import com.google.firebase.Timestamp;
import java.util.regex.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utils {

  public static Map<String, Object> setMessage(String message, String currentUserEmail) {
    String regex = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(message);

    if (matcher.matches()) return setImageMessage(message, currentUserEmail);

    return setTextMessage(message, currentUserEmail);
  }

  public static Map<String, Object> setTextMessage(String message, String sender) {
    Map<String, Object> messageMap = new HashMap<>();
    messageMap.put("context", message);
    messageMap.put("user", sender);
    messageMap.put("timestamp", Timestamp.now());
    messageMap.put("type", MessageType.TEXT);

    return messageMap;
  }

  public static Map<String, Object> setImageMessage(String imageUrl, String sender) {
    Map<String, Object> messageMap = new HashMap<>();
    messageMap.put("context", imageUrl);
    messageMap.put("user", sender);
    messageMap.put("timestamp", Timestamp.now());
    messageMap.put("type", MessageType.IMAGE);

    return messageMap;
  }

  public static String getDate(Date date) {
    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    return dateFormat.format(date);
  }

  public static String getTimeInHour(Date date) {
    @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    return timeFormat.format(date);
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
