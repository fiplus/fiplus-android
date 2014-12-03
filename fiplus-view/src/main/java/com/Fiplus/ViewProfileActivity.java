package com.Fiplus;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jsfirme on 14-12-02.
 */
public class ViewProfileActivity extends Activity
{
    protected ImageView mImageView;
    protected ListView mEventsList;
    protected TextView mProfileName;
    protected ListView mEventListView;

    private ArrayAdapter<String> listAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        //initialize
        mImageView = (ImageView)findViewById(R.id.profileImage);

        mEventsList = (ListView)findViewById(R.id.interests_list);

        mProfileName = (TextView)findViewById(R.id.profileName);
        mEventListView = (ListView)findViewById(R.id.profileEventListView);

        setProfileImage();
        setProfileName();
        setEventList();
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

    // TODO: Change View Profile Implementation
    private void setProfileImage()
    {
        mImageView.setImageResource(R.drawable.ic_configure);
    }

    private void setProfileName()
    {
        String name = "John Doe";

        //disable application icon from ActionBar
        getActionBar().setDisplayShowHomeEnabled(false);

        setTitle(name + "'s Profile");
        mProfileName.setText(name);
    }

    // TODO: layout for view profile event list
    private void setEventList()
    {
        String[] events = new String[] { "Event 1", "Event 2", "Event 3", "Event 4" };
        ArrayList<String> eventsList = new ArrayList<String>();
        eventsList.addAll( Arrays.asList(events) );

        // Create ArrayAdapter using the planet list.
        listAdapter = new ArrayAdapter<String>(this, R.layout.item_view_profile_activities, eventsList);

        // If you passed a String[] instead of a List<String>
        // into the ArrayAdapter constructor, you must not add more items.
        // Otherwise an exception will occur.
        listAdapter.add( "Event 5" );
        listAdapter.add( "Event 6" );
        listAdapter.add( "Event 7" );

        // Set the ArrayAdapter as the ListView's adapter.
        mEventListView.setAdapter( listAdapter );
    }
}
