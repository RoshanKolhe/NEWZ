package com.Newz.newz;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {
    public static final String channel1ID="channel1";
    public static final String channel1name="morning";

    public static final String channel2ID="channel2";
    public static final String channel2name="evening";

    NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            createChannels();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels(){
        NotificationChannel channel1=new NotificationChannel(channel1ID,channel1name, NotificationManager.IMPORTANCE_DEFAULT);
        channel1.enableLights(true);
        channel1.enableVibration(true);
        channel1.setLightColor(R.color.flat_blue);
        channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channel1);

        NotificationChannel channel2=new NotificationChannel(channel2ID,channel2name, NotificationManager.IMPORTANCE_DEFAULT);
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.setLightColor(R.color.flat_blue);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(channel2);

    }
    public NotificationManager getManager(){
        if(notificationManager==null){
            notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getChannel1Notification(String title,String Message){
        Intent resIntent=new Intent(this,MainActivity.class);
        PendingIntent pendingIntentres=PendingIntent.getActivity(this,1,resIntent,PendingIntent.FLAG_UPDATE_CURRENT);

//       // Intent intent=new Intent(Home.this,RemindNotification.class);
////                PendingIntent pendingIntent=PendingIntent.getBroadcast(Home.this,0,intent,0);
//                AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
//                long remindtime=System.currentTimeMillis();
//                long timer=5000*10;
//                alarmManager.set(AlarmManager.RTC_WAKEUP,remindtime+timer,pendingIntentres);

        return  new NotificationCompat.Builder(getApplicationContext(),channel1ID)
                .setSmallIcon(R.drawable.ic_new)
                .setContentTitle(title)
                .setContentText(Message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntentres);
    }

    public NotificationCompat.Builder getChannel2Notification(String title,String Message){
        return  new NotificationCompat.Builder(getApplicationContext(),channel2ID)
                .setSmallIcon(R.drawable.ic_alert)
                .setContentTitle(title)
                .setContentText(Message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }
}
