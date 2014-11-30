package com.Fiplus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import fragments.FragmentRecentActivities;
import fragments.FragmentWhatsHappening;


public class RecentActivitiesActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstancesState) {
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



}
