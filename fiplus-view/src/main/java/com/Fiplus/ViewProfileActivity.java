package com.Fiplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wordnik.client.api.UsersApi;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import adapters.EventListAdapter;
import model.EventListItem;
import utils.IAppConstants;
import utils.LocationUtil;
import utils.PrefUtil;

public class ViewProfileActivity extends Activity
{
    public static final String TAG = ViewProfileActivity.class
            .getSimpleName();

    private CheckBox mFavoriteStar;
    private ImageView mImageView;
    private TextView mProfileName;
    private FlowLayout mInterestList;
    private String mUserId;
    private TextView mNoRecent;

    private ListView mEventsList;
    private EventListAdapter mEventListAdapter;

    ArrayList<EventListItem> eventList;

    private String userName;
    private ArrayList<String> userInterest;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Tracker t = ((FiplusApplication)this.getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);

        userName = getIntent().getExtras().getString("userName");
        userInterest = getIntent().getExtras().getStringArrayList("userInterest");
        mUserId = getIntent().getExtras().getString("userId");

        progressDialog= ProgressDialog.show(ViewProfileActivity.this, getString(R.string.view_profile_progress_bar_title) + "...",
                getString(R.string.progress_dialog_text), true);

        //initialize
        mNoRecent = (TextView) findViewById(R.id.no_recent_activities);
        mImageView = (ImageView)findViewById(R.id.profileImage);
        mProfileName = (TextView)findViewById(R.id.profileName);
        mInterestList = (FlowLayout)findViewById(R.id.profileInterestLayout);
        mFavoriteStar = (CheckBox)findViewById(R.id.checkBox);

        //this means ths current user clicked his own profile
        if(mUserId.equalsIgnoreCase(PrefUtil.getString(getApplicationContext(), IAppConstants.USER_ID, null)))
        {
            mFavoriteStar.setVisibility(View.GONE);
        }

        mFavoriteStar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    AddFavorite addFavorite = new AddFavorite();
                    addFavorite.execute();
                }
                else
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

        mEventsList = (ListView)findViewById(R.id.profileEventListView);
        mEventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String sEventID = mEventListAdapter.getItem(position).getEventId();
                Intent intent = new Intent(getBaseContext(), ViewEventActivity.class);
                intent.putExtra("eventID", sEventID);
                intent.putExtra("pastID", true);
                startActivity(intent);
            }
        });

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


    private void setProfile()
    {
        int padding = getResources().getDimensionPixelOffset(R.dimen.tags_padding);
        int size = userInterest.size();

        progressDialog.dismiss();

        mProfileName.setText(userName);

        if(size == 0)
        {
            TextView interest = new TextView(getBaseContext());
            interest.setText("No Interest(s) Listed");
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

    private void setEventList(List<com.wordnik.client.model.Activity> current, List<com.wordnik.client.model.Activity> past)
    {
        int image;

        if (current == null || past == null)
            return;

        if(current.size() == 0 && past.size() == 0)
        {
            mNoRecent.setVisibility(View.VISIBLE);
        }

        ArrayList<EventListItem> eventList = new ArrayList<EventListItem>();

        //add current activities
        for(int i = 0; i < current.size(); i++)
        {
            if(current.get(i).getIs_cancelled())
            {
                image = R.mipmap.ic_cancelled;
            }
            else if(current.get(i).getIs_confirmed())
            {
                image = R.mipmap.ic_confirm;
            }
            else
            {
                image = R.mipmap.ic_event;
            }

            eventList.add(new EventListItem(
                    image,
                    current.get(i).getName(),
                    LocationUtil.getLocationStrings(current.get(i).getLocations(),   getBaseContext()),
                    current.get(i).getTimes(),
                    ((Integer)current.get(i).getNum_attendees().intValue()).toString(),
                    current.get(i).getActivity_id()));
        }

        //add past activities
        for(int i = 0; i < past.size(); i++)
        {
            eventList.add(new EventListItem(
                    R.mipmap.ic_past,
                    past.get(i).getName(),
                    LocationUtil.getLocationStrings(past.get(i).getLocations(),   getBaseContext()),
                    past.get(i).getTimes(),
                    ((Integer)past.get(i).getNum_attendees().intValue()).toString(),
                    past.get(i).getActivity_id()));
        }

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
            usersApi.getInvoker().setContext(getBaseContext());
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
            usersApi.getInvoker().setContext(getBaseContext());
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
        protected List<com.wordnik.client.model.Activity> responseCurrent;
        protected List<com.wordnik.client.model.Activity> responsePast;
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
            usersApi.getInvoker().setContext(getBaseContext());
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try{
                responseCurrent = usersApi.getActivities(mUserId, false, true);
            } catch (Exception e) {
                return e.getMessage();
            }

            try{
                responsePast = usersApi.getActivities(mUserId, true, false);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (responseCurrent != null && responsePast !=null)
                setEventList(responseCurrent, responsePast);
            progressDialog.dismiss();
        }
    }
}
