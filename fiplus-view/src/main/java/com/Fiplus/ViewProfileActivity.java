package com.Fiplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.UserProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapters.EventListAdapter;
import model.EventListItem;
import utils.IAppConstants;

public class ViewProfileActivity extends Activity
{
    public static final String TAG = ViewProfileActivity.class
            .getSimpleName();

    private ImageView mImageView;
    private TextView mProfileName;
    private TextView mProfileGender;
    private TextView mProfileCountry;
    private ListView mEventsList;
    private EventListAdapter mEventListAdapter ;
    ArrayList<EventListItem> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);

        Bundle b = getIntent().getExtras();
        final String mUserID = b.getString("userID");

        //initialize
        mImageView = (ImageView)findViewById(R.id.profileImage);
        mProfileName = (TextView)findViewById(R.id.profileName);
        mProfileGender = (TextView)findViewById(R.id.profileGender);
        mProfileCountry = (TextView)findViewById(R.id.profileCountry);

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

        GetOtherProfileTask profileView = new GetOtherProfileTask(mUserID);
        profileView.execute();
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

    // TODO: layout for view profile event list
    private void setEventList()
    {
        eventList = new ArrayList<EventListItem>();

        //eventList.add(new EventListItem(R.drawable.ic_configure, "First Near You Event", "Saint John", "4:30PM", "4 Attendees"));
        //eventList.add(new EventListItem(R.drawable.ic_activities, "Second Near You Event", "Calgary", "10:30PM", "4 Attendees"));

        mEventListAdapter = new EventListAdapter(this, eventList, TAG);
        mEventsList.setAdapter(mEventListAdapter);
    }

    class GetOtherProfileTask extends AsyncTask<Void, Void, String> {

        protected ProgressDialog progressDialog;
        protected UserProfile response;
        protected String sUserID;

        public GetOtherProfileTask (String s)
        {
            sUserID = s;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog= ProgressDialog.show(ViewProfileActivity.this, getString(R.string.view_profile_progress_bar_title) + "...", getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(Void... params) {

            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                response = usersApi.getUserProfile(sUserID);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            Address addr;
            List<Address> addressList;

            progressDialog.dismiss();

            if(response == null)
            {
                Toast.makeText(getBaseContext(), "User id = {" + sUserID + "}", Toast.LENGTH_LONG).show();
                return;
            }

            if(response.getUsername() != null)
            {
                mProfileName.setText(response.getUsername());
            }

            if(response.getGender() != null)
            {
                mProfileGender.setText(response.getGender());
            }

            if(response.getLocation().getLongitude() != null)
            {
                try {
                    Geocoder geocoder = new Geocoder(getBaseContext(), Locale.CANADA);
                    addressList = geocoder.getFromLocation(response.getLocation().getLatitude(),
                            response.getLocation().getLongitude(),
                            1);
                    if (addressList != null && addressList.size() > 0)
                    {
                        addr = addressList.get(0);
                        String addressText = addr.getCountryName();
                        // Return the text
                        mProfileCountry.setText(addressText);
                    }
                } catch (IOException e) {
                    Log.e("View Other Profile", e.getMessage());
                }
            }

        }
    }
}
