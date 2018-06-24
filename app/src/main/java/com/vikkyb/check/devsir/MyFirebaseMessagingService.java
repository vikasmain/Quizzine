package com.vikkyb.check.devsir;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
//Base class for receiving messages from Firebase Cloud Messaging.
//
//Extending this class is required to be able to handle downstream messages. It also provides functionality to automatically display notifications, and has methods that are invoked to give the status of upstream messages.
//
//Override base class methods to handle any events required by the application. Methods are invoked on a background thread.
//it contains various functions like onMessageReceived(),onMessageSent(),onMessageDeleted().

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "Dev Sir";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
       // Called when a message is received.

        //This is also called when a notification message is received while the app is in the foreground.
        // The notification parameters can be retrieved with getNotification().

        Log.e(TAG, "Message Body: " + remoteMessage.getNotification().getBody());

        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    //generating push notification
    private void sendNotification(String title, String messageBody) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.chat)
                .setContentTitle(title)
                .setContentText(messageBody)

                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}