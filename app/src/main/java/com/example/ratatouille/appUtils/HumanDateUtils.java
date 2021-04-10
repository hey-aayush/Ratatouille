package com.example.ratatouille.appUtils;

import java.util.Date;

public class HumanDateUtils {

    public static String durationFromNow(Date startDate) {

        long different = System.currentTimeMillis() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String output = "";
        if (elapsedDays > 0) output += elapsedDays + "days ";
        else if ( elapsedHours > 0) output += elapsedHours + " hours ";
        else if (elapsedMinutes > 0) output += elapsedMinutes + " minutes ";
        else if ( elapsedSeconds > 0) output += elapsedSeconds + " seconds ";

        return output+"ago";
    }
}
