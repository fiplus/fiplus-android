package com.Fiplus;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fragments.FragmentMyEvents;

public class MyEventsActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstancesState) {
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
