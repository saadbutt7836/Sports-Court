package com.canndecsolutions.garrisongamerss.Utility;

import java.util.concurrent.TimeUnit;

public class Utility {


    //    Time Stamp Handler
    public static String TimeStampHandle(double timeStamp) {

        long time = (long) timeStamp;

        long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time);
        long hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - time);
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - time);

        String timeAgo = "";

        if (seconds < 60) {
            timeAgo = seconds + " sec";
        } else if (minutes < 60) {
            timeAgo = minutes + " min";
        } else if (hours == 1) {
            timeAgo = hours + " hr";
        } else if (hours < 24) {
            timeAgo = hours + " hrs";
        } else if (days % 7 == 0) {
            long week = days / 7;
            timeAgo = week + " w";
        } else {
            timeAgo = days + " d";
        }

        return timeAgo;
    }


}
