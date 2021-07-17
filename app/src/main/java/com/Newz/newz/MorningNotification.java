package com.Newz.newz;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MorningNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent resIntent=new Intent(context,MainActivity.class);
        PendingIntent pendingIntentres=PendingIntent.getActivity(context,2,resIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"morning")
                .setSmallIcon(R.drawable.ic_new)
                .setContentTitle("New Tasks Are Available !")
                .setContentText("Complete Your Daily & Special Tasks !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntentres);

        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(300,builder.build());
    }
}
