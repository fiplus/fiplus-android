package com.Fiplus;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.Time;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import utils.IAppConstants;
import utils.PrefUtil;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GcmMessageProcessor extends IntentService {
    public static final String TAG = GcmMessageProcessor.class.getSimpleName();
    public static final String FROM_NOTIFICATION = "from_notification";

    NotificationCompat.Builder mBuilder;
    private static int sNotificationId = 1;

    private static final String NEW_ACTIVITY_GROUP = "new_activity_group";
    private static final String FIRM_UP_GROUP = "firm_up_group";

    /** Push notification message types */
    private static final String NEW_ACTIVITY_TYPE = "new_activity";
    private static final String CANCELLED_ACTIVITY_TYPE = "cancelled_activity";
    private static final String FIRM_UP_ACTIVITY_TYPE = "firm_up";

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

                switch(extras.getString("type")) {
                    case NEW_ACTIVITY_TYPE:
                    case CANCELLED_ACTIVITY_TYPE:
                        // The activity list has changed for the user so set event cache to invalid
                        PrefUtil.putBoolean(getApplicationContext(), IAppConstants.BEFIT_CACHE_VALID_FLAG, false);
                        PrefUtil.putBoolean(getApplicationContext(), IAppConstants.INTEREST_EVENTS_CACHE_VALID_FLAG, false);
                        activityNotification(extras.getString("message"), extras.getString("activityId"));
                        break;
                    case FIRM_UP_ACTIVITY_TYPE:
                        String time = null, location = null;

                        try {
                            JSONObject reader = new JSONObject(extras.getString("time"));

                            long start = reader.getLong("start");
                            long end = reader.getLong("start");
                            Date d1 = new Date(start);
                            Date d2 = new Date(end);
                            String format = "EEE MMM d, h:m a";

                            String format2 = format;
                            String middle = " to ";
                            boolean curYear = d1.getYear() == new Date().getYear();
                            boolean sameMonth = d1.getMonth() == d2.getMonth();
                            boolean sameDay = d1.getDate() == d2.getDate();

                            if(sameDay && sameMonth)
                            {
                                format2 = "h:m a";
                                middle = " to ";
                            }
                            if(!curYear)
                            {
                                format2 += ", yyyy";
                            }

                            SimpleDateFormat sdf1 = new SimpleDateFormat(format);
                            SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
                            time = sdf1.format(d1) + middle + sdf2.format(d2);

                        } catch (JSONException e) {
                            // No Time, ignore
                        }

                        try {
                            JSONObject reader = new JSONObject(extras.getString("location"));
                            location = reader.getString("address");
                        } catch (JSONException e) {
                            // No Location, ignore
                        }
                        firmUpNotification(extras.getString("activityId"), extras.getString("Name"), time, location);
                        break;
                    default:
                        break;
                }
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void firmUpNotification(String activityId, String name, String time, String location)
    {
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

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.fiplus)
                .setContentText(name + " will be at: ")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(time)
                        .setBigContentTitle(name + " will be at: ")
                        .addLine(location)
                        .setSummaryText(location))
                .setGroup(FIRM_UP_GROUP)
                .setGroupSummary(true)
                ;
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(sNotificationId++, mBuilder.build());
    }

    private void activityNotification(String msg, String activityId) {

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ViewEventActivity.class);
        resultIntent.putExtra(ViewEventActivity.EXTRA_EVENT_ID, activityId);

        // Boolean which indicates that activity is started from notification; useful for tracking stats
        resultIntent.putExtra(FROM_NOTIFICATION, true);

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
    }
}
