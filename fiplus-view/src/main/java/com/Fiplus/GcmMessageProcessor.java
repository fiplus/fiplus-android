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
                        String message = extras.getString("Name") + " is confirmed for:\n";
                        Time time = (Time) extras.get("time");
                        Location loc = (Location)extras.get("location");
                        if(time != null)
                        {
                            Date d1 = new Date(time.getStart().longValue());
                            Date d2 = new Date(time.getEnd().longValue());
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
                            message += sdf1.format(d1) + middle + sdf2.format(d2) + "\n";
                        }
                        else if (loc != null)
                        {
                            Address addr;
                            List<Address> addressList;
                            try {
                                Geocoder geocoder = new Geocoder(getBaseContext(), Locale.CANADA);
                                addressList = geocoder.getFromLocation(loc.getLatitude(),
                                        loc.getLongitude(), 1);

                                if (addressList != null && addressList.size() > 0) {
                                    addr = addressList.get(0);
                                    String addressText = String.format(
                                            "%s, %s",
                                            // If there's a street address, add it
                                            addr.getMaxAddressLineIndex() > 0 ? addr.getAddressLine(0) : "",
                                            // Locality is usually a city
                                            addr.getLocality() != null ? addr.getLocality() : "");
                                    message += addressText + "\n";
                                }
                            } catch (IOException e) {
                                Log.e("View Event", e.getMessage());
                            }
                        }
                        activityNotification(message, extras.getString("activityId"));
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
