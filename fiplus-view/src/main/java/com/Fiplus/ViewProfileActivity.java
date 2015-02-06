package com.Fiplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

import model.EventListItem;

public class ViewProfileActivity extends Activity
{
    public static final String TAG = ViewProfileActivity.class
            .getSimpleName();

    private ImageView mImageView;
    private TextView mProfileName;
    //private TextView mProfileGender;
    //private TextView mProfileCountry;
    //private ListView mEventsList;
    //private EventListAdapter mEventListAdapter ;
    private FlowLayout mInterestList;
    ArrayList<EventListItem> eventList;

    private String userName;
    private ArrayList<String> userInterest;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);

        userName = getIntent().getExtras().getString("userName");
        userInterest = getIntent().getExtras().getStringArrayList("userInterest");

        progressDialog= ProgressDialog.show(ViewProfileActivity.this, getString(R.string.view_profile_progress_bar_title) + "...",
                getString(R.string.progress_dialog_text), true);

        //initialize
        mImageView = (ImageView)findViewById(R.id.profileImage);
        mProfileName = (TextView)findViewById(R.id.profileName);
        mInterestList = (FlowLayout)findViewById(R.id.profileInterestLayout);

        //TODO: View Other Profile - Recent Activities
//        mEventsList = (ListView)findViewById(R.id.profileEventListView);
//        mEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//
//            }
//        });

//        mEventsList.setOnTouchListener(new ListView.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                        // Disallow ScrollView to intercept touch events.
//                        v.getParent().requestDisallowInterceptTouchEvent(true);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        // Allow ScrollView to intercept touch events.
//                        v.getParent().requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//
//                // Handle ListView touch events.
//                v.onTouchEvent(event);
//                return true;
//            }
//        });

        setProfile();
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

    // TODO: layout for view profile recent event list
    private void setEventList()
    {
//        EventListAdapter mEventListAdapter ;
//
//        eventList = new ArrayList<EventListItem>();
//
//        eventList.add(new EventListItem(R.drawable.ic_configure, "Dummy Event", "Saint John", "4:30PM", "4 Attendees"));
//        eventList.add(new EventListItem(R.drawable.ic_activities, "Second Near You Event", "Calgary", "10:30PM", "4 Attendees"));
//
//        mEventListAdapter = new EventListAdapter(this, eventList, TAG);
//        mEventsList.setAdapter(mEventListAdapter);
    }

    private void setProfile()
    {
        int padding = getResources().getDimensionPixelOffset(R.dimen.tags_padding);
        int size = userInterest.size();

        progressDialog.dismiss();

        mProfileName.setText(userName);

        if(size == 0)
        {
            TextView interest = new TextView(getBaseContext());
            interest.setText("No Interest Listed");
            interest.setPadding(padding, 0, 0, 0);
            interest.setTextColor(Color.GRAY);
            mInterestList.addView(interest);
        }
        else
        {
            for(int i= 0; i < size; i++)
            {
                TextView interest = new TextView(getBaseContext());
                interest.setText(userInterest.get(i));
                interest.setTextColor(Color.BLACK);
                interest.setBackgroundResource(R.drawable.button_tags);
                interest.setPadding(padding, 0, padding, 0);
                mInterestList.addView(interest);
            }
        }

    }
}
