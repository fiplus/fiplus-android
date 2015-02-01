package com.Fiplus;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
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

                sendNotification(extras.getString("message"));
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String msg) {
        mBuilder = new NotificationCompat.Builder(this)
                        // TODO: Set icon (Allan)
                        .setSmallIcon(R.drawable.fiplus)
                        .setContentTitle("Fi+")
                        .setContentText(msg);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // TODO: Change 1 to private int mId if required. mId allows you to update the notification later on. (Allan)
        mNotificationManager.notify(1, mBuilder.build());
        /*
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainScreenActivity.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainScreenActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        */
    }
}
