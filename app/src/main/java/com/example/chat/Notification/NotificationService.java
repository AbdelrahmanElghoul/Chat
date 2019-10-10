package com.example.chat.Notification;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.chat.Rooms.RoomsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import timber.log.Timber;

public class NotificationService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Timber.d(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        String title = data.get(PushNotification.TITLE_KEY);
        String message = data.get(PushNotification.MESSAGE_KEY);

        Context context=this;
        if(!(context instanceof RoomsActivity))
             NotificationUtil.ShowNotification(this,title,message);
    }
}
