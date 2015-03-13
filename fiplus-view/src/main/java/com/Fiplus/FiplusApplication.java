package com.Fiplus;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by lostape on 12/03/15.
 */
public class FiplusApplication extends Application {

    private Tracker appTracker;
    public static final String VIEWS_CATEGORY = "views";
    public static final String VIEWED_EVENT_ACTION = "viewed event action";
    public static final String CLICKED_NOTIFICATION_ACTION = "clicked_notification";

    public static final String ACCOUNTS_CATEGORY = "accounts";
    public static final String SIGNUP_ACTION = "signedup";

    @Override
    public void onCreate() {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        appTracker = analytics.newTracker(R.xml.app_tracker);
        super.onCreate();
    }

    public Tracker getTracker() {
        return appTracker;
    }
}
