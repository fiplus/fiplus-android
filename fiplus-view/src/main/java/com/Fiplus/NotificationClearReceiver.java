package com.Fiplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationClearReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Notification cleared means we need to reset the notification to start building a new stack
        switch(intent.getExtras().getInt(GcmMessageProcessor.NOTIFICATION_ID)) {
            case GcmMessageProcessor.ACTIVITY_GROUP_NOTIF_ID:
                GcmMessageProcessor.newActivitiesStyle = new NotificationCompat.InboxStyle();
                GcmMessageProcessor.sNewActivitiesIsStacked = false;
                break;
            case GcmMessageProcessor.CANCELLED_ACTIVITY_ID:
                GcmMessageProcessor.cancelledActivitiesStyle = new NotificationCompat.InboxStyle();
                GcmMessageProcessor.sCancelledActivitiesIsStacked = false;
                break;
            default:
                break;
        }
    }
}
