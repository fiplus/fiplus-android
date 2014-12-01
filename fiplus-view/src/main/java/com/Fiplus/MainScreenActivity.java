package com.Fiplus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;

import adapters.NavDrawerListAdapter;
import fragments.FragmentWhatsHappening;
import model.NavDrawerItem;

/**
 * Main screen
 */
public class MainScreenActivity extends FragmentActivity //implements TabListener {
{
    private static final String TAG = MainScreenActivity.class.getSimpleName();

    private float lastTranslate = 0.0f;
    private FrameLayout mainScreenFrame;

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

        mTitle = mDrawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_leftdrawer);
        mainScreenFrame = (FrameLayout) findViewById(R.id.frame_container);

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

        if (savedInstancesState == null)
        {
            // on first time display What's happening fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, FragmentWhatsHappening.newInstance(), FragmentWhatsHappening.TAG).commit();
        }
    }

    private void functionAddDrawerItems()
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

        return super.onOptionsItemSelected(item);
    }

	/*
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

    private class DrawerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            navigateTo(position);
        }
    }

    private void navigateTo(int position)
    {
        Intent intent;

        // TODO: Options fragments/intents here
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
                break;
            case 3: //Settings
                break;
        }

        //for slide in and out transition
        // TODO: fix black flicker
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

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
