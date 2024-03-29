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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    public static NotificationCompat.InboxStyle newActivitiesStyle = new NotificationCompat.InboxStyle();
    public static boolean sNewActivitiesIsStacked = false;

    public static NotificationCompat.InboxStyle cancelledActivitiesStyle = new NotificationCompat.InboxStyle();
    public static boolean sCancelledActivitiesIsStacked = false;

    /** The group just makes sure the different notification types appear next to each other */
    private static final String FIPLUS_NOTIFS_GROUP = "fiplus_group";
    public static final int ACTIVITY_GROUP_NOTIF_ID = 1;
    public static int FIRM_UP_GROUP_NOTIF_ID = 2;
    public static final int CANCELLED_ACTIVITY_ID = 3;

    /** Push notification message types */
    private static final String NEW_ACTIVITY_TYPE = "new_activity";
    private static final String CANCELLED_ACTIVITY_TYPE = "cancelled_activity";
    private static final String FIRM_UP_ACTIVITY_TYPE = "firm_up";

    /** Notification Intent Actions */
    public static final String ACTION_NOTIFICATION_CLEARED = "notification_cleared";
    public static final String NOTIFICATION_ID = "notif_id";

    public GcmMessageProcessor() {
        super(GcmMessageProcessor.class.getSimpleName());
    }

    @Override
    public void onHandleIntent(Intent intent) {
        // The activity list has changed for the user so set event cache to invalid
        PrefUtil.putBoolean(getApplicationContext(), IAppConstants.BEFIT_CACHE_VALID_FLAG, false);
        PrefUtil.putBoolean(getApplicationContext(), IAppConstants.INTEREST_EVENTS_CACHE_VALID_FLAG, false);
        if(!PrefUtil.getBoolean(getBaseContext(),IAppConstants.PUSH_NOTIFICATIONS_ENABLED_SETTING, true)) return;

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                switch(extras.getString("type")) {
                    case NEW_ACTIVITY_TYPE:
                        newActivityNotification(extras.getString("message"), extras.getString("activityId"));
                        break;
                    case CANCELLED_ACTIVITY_TYPE:
                        cancelledActivityNotification(extras.getString("message"), extras.getString("activityId"));
                        break;
                    case FIRM_UP_ACTIVITY_TYPE:
                        String time = null, location = null;

                        try {
                            JSONObject reader = new JSONObject(extras.getString("time"));

                            long start = reader.getLong("start");
                            long end = reader.getLong("end");
                            Date d1 = new Date(start);
                            Date d2 = new Date(end);
                            String format = "EEE MMM d, h:mm a";

                            String format2 = format;
                            String middle = " to ";
                            boolean curYear = d1.getYear() == new Date().getYear();
                            boolean sameMonth = d1.getMonth() == d2.getMonth();
                            boolean sameDay = d1.getDate() == d2.getDate();

                            if(sameDay && sameMonth)
                            {
                                format2 = "h:mm a";
                                middle = " to ";
                            }
                            if(!curYear)
                            {
                                format2 += ", yyyy";
                            }

                            SimpleDateFormat sdf1 = new SimpleDateFormat(format);
                            SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
                            time = sdf1.format(d1) + middle + sdf2.format(d2);

                            reader = new JSONObject(extras.getString("location"));
                            location = reader.getString("address");

                            firmUpNotification(extras.getString("activityId"), extras.getString("Name"), time, location);
                        } catch (JSONException e) {
                            // No Location or no time, ignore
                        }
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
                .setContentText(name + " has been confirmed... ")
                .setContentTitle("Event Firmed Up!")
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(time)
                        .setBigContentTitle(name + " will be at: ")
                        .addLine(location))
                .setGroup(FIPLUS_NOTIFS_GROUP)
                .setGroupSummary(true);

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(FIRM_UP_GROUP_NOTIF_ID++, mBuilder.build());
    }

    private void newActivityNotification(String msg, String activityId) {

        Intent resultIntent;
        if(sNewActivitiesIsStacked) {
            resultIntent = new Intent(this, MainScreenActivity.class);

        } else {
            resultIntent = new Intent(this, ViewEventActivity.class);
            resultIntent.putExtra(ViewEventActivity.EXTRA_EVENT_ID, activityId);
        }
        sNewActivitiesIsStacked = true;

        // Boolean which indicates that activity is started from notification; useful for tracking stats
        resultIntent.putExtra(FROM_NOTIFICATION, true);
        resultIntent.putExtra(NOTIFICATION_ID, ACTIVITY_GROUP_NOTIF_ID);

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

        newActivitiesStyle.setBigContentTitle("New Events!");
        newActivitiesStyle.addLine(msg);

        Intent notificationClearedIntent = new Intent(ACTION_NOTIFICATION_CLEARED);
        notificationClearedIntent.putExtra(NOTIFICATION_ID, ACTIVITY_GROUP_NOTIF_ID);

        mBuilder =  new NotificationCompat.Builder(this)
                .setContentTitle("Fi+")
                .setContentText(msg)
                .setSmallIcon(R.mipmap.fiplus)
                .setStyle(newActivitiesStyle)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setGroup(FIPLUS_NOTIFS_GROUP)
                .setDeleteIntent(PendingIntent.getBroadcast(this, 0, notificationClearedIntent, 0));

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(ACTIVITY_GROUP_NOTIF_ID, mBuilder.build());
    }

    private void cancelledActivityNotification(String msg, String activityId) {

        Intent resultIntent;
        if(sCancelledActivitiesIsStacked) {
            resultIntent = new Intent(this, MyEventsActivity.class);

        } else {
            resultIntent = new Intent(this, ViewEventActivity.class);
            resultIntent.putExtra(ViewEventActivity.EXTRA_EVENT_ID, activityId);
        }
        sCancelledActivitiesIsStacked = true;

        // Boolean which indicates that activity is started from notification; useful for tracking stats
        resultIntent.putExtra(FROM_NOTIFICATION, true);
        resultIntent.putExtra(NOTIFICATION_ID, CANCELLED_ACTIVITY_ID);

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

        cancelledActivitiesStyle.setBigContentTitle("Events Cancelled");
        cancelledActivitiesStyle.addLine(msg);

        Intent notificationClearedIntent = new Intent(ACTION_NOTIFICATION_CLEARED);
        notificationClearedIntent.putExtra(NOTIFICATION_ID, CANCELLED_ACTIVITY_ID);

        mBuilder =  new NotificationCompat.Builder(this)
                .setContentTitle("Fi+")
                .setContentText(msg)
                .setSmallIcon(R.mipmap.fiplus)
                .setStyle(cancelledActivitiesStyle)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setGroup(FIPLUS_NOTIFS_GROUP)
                .setDeleteIntent(PendingIntent.getBroadcast(this, 0, notificationClearedIntent, 0));

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(CANCELLED_ACTIVITY_ID, mBuilder.build());
    }
}
