package com.Fiplus;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

// TODO: Complete notification (Allan)
public class GcmBroadcastReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    private Context savedContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: Update push notification cases
        sendNotification(intent.getDataString(), context);

    }


    private void sendNotification(String msg, Context context) {
        savedContext = context;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(savedContext)
                        // TODO: Set icon (Allan)
                        .setSmallIcon(0)
                        .setContentTitle("Fi+")
                        .setContentText(msg);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(savedContext, MainScreenActivity.class);

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(savedContext);
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
        NotificationManager mNotificationManager =
                (NotificationManager) savedContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // TODO: Change 1 to private int mId if required. mId allows you to update the notification later on. (Allan)
        mNotificationManager.notify(1, mBuilder.build());
    }
}

