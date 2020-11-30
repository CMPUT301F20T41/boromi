package com.team41.boromi.dbs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;

/**
 * Sends and receives a push notification
 */
public class BoromiFirebaseMessaging extends FirebaseMessagingService {

  private static final String TAG = "BoromiFirebaseMessaging";

  @Override
  public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
    String notificationTitle = null;
    String notificationBody = null;

    // Check if message contains a notification payload
    if (remoteMessage.getNotification() != null) {
      Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
      notificationTitle = remoteMessage.getNotification().getTitle();
      notificationBody = remoteMessage.getNotification().getBody();
    }

    // If you want to fire a local notification (that notification on the top of the phone screen)
    // you should fire it from here
    sendLocalNotification(notificationTitle, notificationBody);
  }

  /**
   * Sends a local notification
   * @param notificationTitle Title of the notification
   * @param notificationBody Body of the notification
   */
  private void sendLocalNotification(String notificationTitle, String notificationBody) {
    Intent intent = new Intent(this, BookActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT
    );

    Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
            .setAutoCancel(true)   //Automatically delete the notification
            .setSmallIcon(R.drawable.logo_boromi_text) //Notification icon
            .setContentIntent(pendingIntent)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setSound(defaultSoundUri);

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(1234, notificationBuilder.build());
  }
}
