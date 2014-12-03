package com.Fiplus;

/**
 * Created by Allan on 03/12/2014.
 */
public class FavouriteUsersActivity extends MainScreenActivity {
    /** @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        // TODO: Create fragment_favourite_users layout
        setContentView(R.layout.fragment_favourite_users);
    }


    /**
     * public class MyEventsActivity extends MainScreenActivity {
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
     */
}
