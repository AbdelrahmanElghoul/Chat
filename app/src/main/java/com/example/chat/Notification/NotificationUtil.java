package com.example.chat.Notification;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.chat.R;
import com.example.chat.Rooms.RoomsActivity;

public class NotificationUtil {


    private static final String Notify_Channel_ID="reminder_channel";
    private static final int Notify_ID=18323;

    private static PendingIntent contentIntent(Context context){
        Intent startActivityIntent=new Intent(context, RoomsActivity.class);
        return PendingIntent.getActivity(
                context,
                Notify_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    public static void ShowNotification(Context context,String Title,String msg){
        NotificationManager notify= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    Notify_Channel_ID,
                    "channel",
                    NotificationManager.IMPORTANCE_HIGH);
            notify.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder=
                new NotificationCompat.Builder(context,Notify_Channel_ID)
                        .setColor(ContextCompat.getColor(context,R.color.colorAccent))
                        .setContentTitle(Title)
                        .setContentText(msg)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentIntent(contentIntent(context))
                        .setAutoCancel(true);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT <Build.VERSION_CODES.O)
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        notify.notify(Notify_ID,builder.build());
    }

}
