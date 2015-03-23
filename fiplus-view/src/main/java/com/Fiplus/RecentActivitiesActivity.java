package com.Fiplus;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import fragments.FragmentRecentActivities;


public class RecentActivitiesActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        Tracker t = ((FiplusApplication)this.getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_recent_activities);

        setTitle(R.string.recent_activities_activity);

        if (savedInstancesState == null)
        {
            // on first time display What's happening fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recent_activities_frame_container, FragmentRecentActivities.newInstance(), FragmentRecentActivities.TAG).commit();
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

}
