package com.zing.secureme.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.zing.secureme.receivers.AlarmReceiver;

public class MyAlarmManager {

    public static AlarmManager getAlarmManager(Context context){
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public static PendingIntent getPendingIntent(Context context, int flag ){
        Intent intent = new Intent(context , AlarmReceiver.class);
        int PENDING_ID = 9876;
        return PendingIntent.getBroadcast(context , PENDING_ID, intent , flag );
    }
}
