package com.Fiplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationClearReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("mytag", "notification cleared");
        // Notification cleared means we need to reset the notification to start building a new stack
        GcmMessageProcessor.style = new NotificationCompat.InboxStyle();
        GcmMessageProcessor.sIsStacked = false;
    }
}
