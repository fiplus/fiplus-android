package com.Fiplus;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import adapters.EventListAdapter;
import model.EventListItem;

/**
 * Created by jsfirme on 14-12-02.
 */
public class ViewProfileActivity extends Activity
{
    public static final String TAG = ViewProfileActivity.class
            .getSimpleName();

    private ImageView mImageView;
    private TextView mProfileName;
    private ListView mEventsList;
    private EventListAdapter mEventListAdapter ;
    ArrayList<EventListItem> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);

        //initialize
        mImageView = (ImageView)findViewById(R.id.profileImage);
        mProfileName = (TextView)findViewById(R.id.profileName);
        setProfile();

        mEventsList = (ListView)findViewById(R.id.profileEventListView);
        setEventList();

        mEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //TODO: Add item click listener for view profile list
            }
        });

        mEventsList.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
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
    private void setProfile()
    {
        String name = "John Doe";

        //disable application icon from ActionBar
        getActionBar().setDisplayShowHomeEnabled(false);

        mImageView.setImageResource(R.drawable.ic_configure);

        setTitle(name + "'s Profile");
        mProfileName.setText(name);
    }

    // TODO: layout for view profile event list
    private void setEventList()
    {
        eventList = new ArrayList<EventListItem>();

        eventList.add(new EventListItem(R.drawable.ic_configure, "First Near You Event", "Saint John", "4:30PM", "4 Attendees"));
        eventList.add(new EventListItem(R.drawable.ic_activities, "Second Near You Event", "Calgary", "10:30PM", "4 Attendees"));

        mEventListAdapter = new EventListAdapter(this, eventList, TAG);
        mEventsList.setAdapter(mEventListAdapter);
    }
}
