package com.Fiplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wordnik.client.api.UsersApi;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import adapters.EventListAdapter;
import model.EventListItem;
import utils.IAppConstants;
import utils.LocationUtil;

public class ViewProfileActivity extends Activity
{
    public static final String TAG = ViewProfileActivity.class
            .getSimpleName();

    private CheckBox mFavoriteStar;
    private ImageView mImageView;
    private TextView mProfileName;
    private FlowLayout mInterestList;
    private String mUserId;

    private ListView mEventsList;
    private EventListAdapter mEventListAdapter;

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
        mUserId = getIntent().getExtras().getString("userId");

        progressDialog= ProgressDialog.show(ViewProfileActivity.this, getString(R.string.view_profile_progress_bar_title) + "...",
                getString(R.string.progress_dialog_text), true);

        //initialize
        mImageView = (ImageView)findViewById(R.id.profileImage);
        mProfileName = (TextView)findViewById(R.id.profileName);
        mInterestList = (FlowLayout)findViewById(R.id.profileInterestLayout);
        mFavoriteStar = (CheckBox)findViewById(R.id.checkBox);

        mFavoriteStar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    AddFavorite addFavorite = new AddFavorite();
                    addFavorite.execute();
                }
                else if(!isChecked)
                {
                    RemoveFavorite removeFavorite = new RemoveFavorite();
                    removeFavorite.execute();
                }
            }
        });

        if(getIntent().getExtras().getBoolean("favourited"))
        {
            mFavoriteStar.setChecked(true);
        }

        //TODO: View Other Profile - Recent Activities
        mEventsList = (ListView)findViewById(R.id.profileEventListView);
        mEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

            }
        });

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
        GetRecentEvents getRecentEvents = new GetRecentEvents();
        getRecentEvents.execute();
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

    private void setEventList(List<com.wordnik.client.model.Activity> activities)
    {
        if (activities == null)
        {
            return;
        }

        ArrayList<EventListItem> eventList = new ArrayList<EventListItem>();

        for(int i = 0; i < activities.size(); i++)
            eventList.add(new EventListItem(
                    R.drawable.ic_configure,
                    activities.get(i).getName(),
                    LocationUtil.getLocationStrings(activities.get(i).getLocations(),   getBaseContext()),
                    activities.get(i).getTimes(),
                    ((Integer)activities.get(i).getNum_attendees().intValue()).toString(),
                    activities.get(i).getActivity_id()));

        mEventListAdapter = new EventListAdapter(this, eventList, TAG);
        mEventsList.setAdapter(mEventListAdapter);
        mEventListAdapter.notifyDataSetChanged();

    }

    private class AddFavorite extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params)
        {
            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);
            try{
                usersApi.addFavourite(mUserId);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }
    }

    private class RemoveFavorite extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params)
        {
            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);
            try{
                usersApi.deleteFavourites(mUserId);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }
    }

    private class GetRecentEvents extends AsyncTask<Void, Void, String>
    {
        protected List<com.wordnik.client.model.Activity> response;
        protected ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog= ProgressDialog.show(ViewProfileActivity.this, getString(R.string.view_event_progress_bar_title) + "s...", getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try{
                response = usersApi.getActivities(mUserId, true, false);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (response != null)
                setEventList(response);
            progressDialog.dismiss();
        }
    }
}
