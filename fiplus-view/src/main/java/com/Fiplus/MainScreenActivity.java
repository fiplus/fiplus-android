package com.Fiplus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import adapters.NavDrawerListAdapter;
import fragments.FragmentWhatsHappening;
import model.NavDrawerItem;

/**
 * Main screen
 */
public class MainScreenActivity extends BaseFragmentActivity
{
    private float lastTranslate = 0.0f;
    private RelativeLayout mainScreenFrame;
    private Button mCreateEventButton;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstancesState)
    {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_mainscreen);


        if(getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean(GcmMessageProcessor.FROM_NOTIFICATION))
        {
            Tracker t = ((FiplusApplication)this.getApplication()).getTracker();
            t.send(new HitBuilders.EventBuilder().setCategory(FiplusApplication.VIEWS_CATEGORY)
                    .setAction(FiplusApplication.CLICKED_NOTIFICATION_ACTION).build());
            // Navigated here from stacked event info notification, need to reset stack
            GcmMessageProcessor.newActivitiesStyle = new NotificationCompat.InboxStyle();
            GcmMessageProcessor.sNewActivitiesIsStacked = false;
        }

        /**
         * Create Event Button
         */
        mCreateEventButton= (Button) findViewById(R.id.create_event_button);
        mCreateEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreateEvent();
            }
        });

        /**
         * For Drawer Navigation Setup
         */
        mTitle = mDrawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_leftdrawer);
        mainScreenFrame = (RelativeLayout) findViewById(R.id.frame_container);

        // set a custom shadow that overlays the main content when the drawer oepns
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,  GravityCompat.START);

        //add nav drawer items to array
        navDrawerItems = new ArrayList<NavDrawerItem>();
        functionAddDrawerItems();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enabling action bar app icon and behaving it as toggle button
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, //icon
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }

            public void onDrawerOpened(View drawerView)
            {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }

            //override this function to slide the main view when navdrawer is open
            @SuppressLint("NewApi")
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                float moveFactor = (mDrawerList.getWidth() * slideOffset);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    mainScreenFrame.setTranslationX(moveFactor);
                }
                else
                {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    mainScreenFrame.startAnimation(anim);

                    lastTranslate = moveFactor;
                }
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        functionCheckInstance(savedInstancesState);
    }

    protected void functionCheckInstance(Bundle savedInstancesState) {
        if (savedInstancesState == null) {
            //on first time display What's happening fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, FragmentWhatsHappening.newInstance(), FragmentWhatsHappening.TAG).commit();
        }
    }

    protected void functionAddDrawerItems()
    {
        // adding nav drawer items to array
        for(int i=0;i<navMenuTitles.length;i++)
        {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
        }

        // Recycle the typed array
        navMenuIcons.recycle();

        //MAY NEED IN THE FUTURE
        //navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbarbuttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    * If you do not have any menus, you still need this function
    * in order to open or close the NavigationDrawer when the user
    * clicking the ActionBar app icon.
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_my_events:
                goToMyEvents();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Creates a new intent for Create Event Activity
     */
    private void goToCreateEvent()
    {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    /**
     * Creates a new intent for My Events Activity
     */
    private void goToMyEvents()
    {
        Intent intent = new Intent(this, MyEventsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
    }

	/**
	 * When using the ActionBarDrawerToggle, you must call it during onPostCreate()
	 * and onConfigurationChanged()
	 */
    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            navigateTo(position);
        }
    }

    protected void navigateTo(int position)
    {
        Intent intent;

        // TODO: Add drawer items once implemented
        switch(position) {
            case 0: //Configure Profile
                intent = new Intent(this, ConfigureProfileActivity.class);
                startActivity(intent);
                break;
            case 1: //Recent Activities
                intent = new Intent(this, RecentActivitiesActivity.class);
                startActivity(intent);
                break;
            case 2: //Favorites
                intent = new Intent(this, FavouriteUsersActivity.class);
                startActivity(intent);
                break;
            case 3: //Settings
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case 4: //logout
                logout();
                break;
        }

        //for slide in and out transition
        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);

        /**
         * TODO: Remove once the options are implemented
         */
        // update selected item then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
