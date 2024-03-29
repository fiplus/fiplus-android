package com.Fiplus;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import fragments.FragmentMyEvents;

public class MyEventsActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstancesState) {
        Tracker t = ((FiplusApplication)this.getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());

        if(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean(GcmMessageProcessor.FROM_NOTIFICATION))
        {
            t = ((FiplusApplication)this.getApplication()).getTracker();
            t.send(new HitBuilders.EventBuilder().setCategory(FiplusApplication.VIEWS_CATEGORY)
                    .setAction(FiplusApplication.CLICKED_NOTIFICATION_ACTION).build());
            // Navigated here from stacked event info notification, need to reset stack
            GcmMessageProcessor.cancelledActivitiesStyle = new NotificationCompat.InboxStyle();
            GcmMessageProcessor.sCancelledActivitiesIsStacked = false;
        }

        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_my_events);

        setTitle(R.string.my_events);

        if (savedInstancesState == null)
        {
            // on first time display What's happening fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.my_events_frame_container, FragmentMyEvents.newInstance(), FragmentMyEvents.TAG).commit();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    /**
     * TODO: Leave these comments in case we switch back My Events as a bottom tab option in the main screen.
     * If so, this class should extend MainScreenActivity instead of FragmentActivity
     */
/*
    @Override
    protected void functionCheckInstance(Bundle savedInstancesState)
    {
        if (savedInstancesState == null)
        {
            // on first time display My Events fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, FragmentMyEvents.newInstance(), FragmentMyEvents.TAG).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
}
