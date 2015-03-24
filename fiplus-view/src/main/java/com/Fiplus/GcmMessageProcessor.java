package com.Fiplus;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GcmMessageProcessor extends IntentService {
    public static final String TAG = GcmMessageProcessor.class.getSimpleName();

    NotificationCompat.Builder mBuilder;
    private static int sNotificationId = 1;

    private static final String NEW_ACTIVITY_GROUP = "new_activity_group";

    public GcmMessageProcessor() {
        super(GcmMessageProcessor.class.getSimpleName());
    }

    @Override
    public void onHandleIntent(Intent intent) {
        // TODO: Update push notification cases
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    sendActivityNotification(extras.getString("message"), extras.getString("activityId"));
                }
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendActivityNotification(String msg, String activityId) {

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ViewEventActivity.class);
        resultIntent.putExtra(ViewEventActivity.EXTRA_EVENT_ID, activityId);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ViewEventActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        // Create the Current Notification
        if(sNotificationId)
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.fiplus)
                .setContentTitle("Fi+")
                .setContentText(msg)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setGroup(NEW_ACTIVITY_GROUP)
                .setGroupSummary(true);


        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(sNotificationId++, mBuilder.build());


        // Create an InboxStyle Summary Notification and add the Current Notification
        if(sNotificationId)
        Notification summaryNotification = new NotificationCompat.Builder(mContext)
                .setContentTitle("New Update")
                .setSmallIcon(R.mipmap.fiplus)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(msg)
                        .setBigContentTitle("New Update")
                .setGroup(NEW_ACTIVITY_GROUP)
                .setGroupSummary(true)
                .build();

        notificationManager.notify(sNotificationId, summaryNotification);
    }
}
