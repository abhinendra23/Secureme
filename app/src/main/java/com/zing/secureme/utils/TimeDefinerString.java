package com.zing.secureme.utils;

import com.zing.secureme.Constants.TimeConstants;

public class TimeDefinerString {
    private static String halfHour = "Every 30 minutes";
    private static String anHour = "Every 1 hour";
    private static String اanHourAndHalf = "Every 1 hour and a half ";
    private static String twoHour = "Every 2 hours";
    private static String threeHour = "Every 3 hours";
    private static String noTime = "Reminder not organized";

    public static String getTimeDefiner(long time){
        if (time == TimeConstants.HALF_HOUR)
            return halfHour;
        if (time == TimeConstants.ONE_HOUR)
            return anHour;
        if (time == TimeConstants.ONE_AND_HALF_HOUR)
            return اanHourAndHalf;
        if (time == TimeConstants.TWO_HOUR)
            return twoHour;
        if (time == TimeConstants.THREE_HOUR)
            return threeHour;

        return noTime;
    }
}
