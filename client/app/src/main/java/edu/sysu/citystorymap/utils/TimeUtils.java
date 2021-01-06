package edu.sysu.citystorymap.utils;

import java.util.Calendar;

public class TimeUtils {
    public static String toTimeString(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String time = year + "-" +
                month + "-" +
                day + " " +
                hour + ":"+
                minute;

        return time;
    }
}
