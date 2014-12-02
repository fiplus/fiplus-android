package com.Fiplus;

import android.os.Bundle;

import fragments.FragmentMyEvents;

public class MyEventsActivity extends MainScreenActivity {
    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        //setContentView(R.layout.fragment_my_events);

        //PUT THIS WHEN CALLING A NEW INTENT
        //finish();
        //intent = new Intent(this, MyEventsActivity.class);
        //startActivity(intent);

    }

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
}