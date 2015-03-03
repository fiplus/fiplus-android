package com.Fiplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wordnik.client.api.ActsApi;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Activity;
import com.wordnik.client.model.Attendee;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.Time;
import com.wordnik.client.model.UserProfile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import utils.DateTimePicker;
import utils.IAppConstants;
import utils.PrefUtil;

public class ViewEventActivity extends FragmentActivity {

    static final String DATEFORMAT = "MMM-dd-yyyy HH:mm a";
    public static final String EXTRA_EVENT_ID = "eventID";

    protected TextView mEventName;
    protected TextView mEventDesc;
    protected Button mJoinEventBtn;
    protected Button mCancelBtn;
    protected LinearLayout mLocationList;
    protected LinearLayout mTimeList;
    protected LinearLayout mAttendeesList;
    protected LinearLayout mSuggestButtonsLayout;
    protected TextView mAttendeesLabel;
    protected Button mSuggestDate;
    protected Button mSuggestTime;

    protected List<Time> mSuggestedTimes = new ArrayList<Time>();
    protected List<Location> mSuggestedLocs = new ArrayList<Location>();
    protected List<UserProfile> mAttendees = new ArrayList<UserProfile>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Bundle b = getIntent().getExtras();
        final String mEventID = b.getString(EXTRA_EVENT_ID);

        mEventName = (TextView) findViewById(R.id.view_event_name);
        mEventDesc = (TextView) findViewById(R.id.view_event_description);
        mLocationList = (LinearLayout) findViewById(R.id.view_event_loc_checkboxes);
        mTimeList = (LinearLayout) findViewById(R.id.view_event_time_checkboxes);
        mAttendeesList = (LinearLayout)findViewById(R.id.view_event_attendees_list);
        mAttendeesLabel = (TextView) findViewById(R.id.view_event_attendees_label);

        GetEventTask getEventTask = new GetEventTask(mEventID);
        getEventTask.execute();

        mJoinEventBtn = (Button) findViewById(R.id.view_event_join_btn);
        mJoinEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinEventTask joinEventTask = new JoinEventTask(mEventID);
                joinEventTask.execute();
            }
        });

        //TODO: (Jobelle) Join Event - Change button label based on user (i.e. isCreator or isJoiner)
        mCancelBtn = (Button) findViewById(R.id.view_event_not_interested_btn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mSuggestButtonsLayout = (LinearLayout) findViewById(R.id.suggest_layout);
        mSuggestDate = (Button) findViewById(R.id.view_event_suggest_date);
        mSuggestTime = (Button) findViewById(R.id.view_event_suggest_time);
        mSuggestTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DateTimePicker getDateTime = new DateTimePicker(ViewEventActivity.this);
                getDateTime.showDateTimePickerDialog();
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

    class GetEventTask extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;
        Activity response;
        String sEventID, sEventDetails = "";
        Attendee attendees;
        Boolean isAJoiner = false;

        public GetEventTask (String s)
        {
            sEventID = s;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog= ProgressDialog.show(ViewEventActivity.this, getString(R.string.view_event_progress_bar_title) + "...", getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(Void... params) {

            ActsApi getEventApi = new ActsApi();
            getEventApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            getEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                response = getEventApi.getActivity(sEventID);
                sEventDetails = response.toString();
                attendees = getEventApi.getAttendees(sEventID, null);
            } catch (Exception e) {
                sEventDetails = e.getMessage();
                Log.e("Error - Get Activity", sEventDetails);
            }

            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);
            UserProfile sUserProfile;

            // to handle bad events
            try
            {
                for(int i=0; i < attendees.getJoiners().size(); i++)
                {
                    try {
                        String sID = attendees.getJoiners().get(i);
                        //check if a user is a joiner of this event
                        if(sID.equalsIgnoreCase(PrefUtil.getString(getApplicationContext(), IAppConstants.USER_ID, null)))
                        {
                            isAJoiner = true;
                        }
                        sUserProfile = usersApi.getUserProfile(sID);
                        mAttendees.add(sUserProfile);
                    } catch (Exception e) {
                        sEventDetails = e.getMessage();
                        Log.e("Error - User Profile", sEventDetails);
                    }
                }
            }
            catch(NullPointerException e)
            {
                sEventDetails = e.getMessage();
                Log.e("Error - Get Joiners", sEventDetails);
            }

            return sEventDetails;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            progressDialog.dismiss();

            //handle badly created events
            if(result.contains("error"))
            {
                Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
            }
            else
            {
                mEventName.setText(response.getName());
                mEventDesc.setText(response.getDescription());
                mSuggestedTimes = response.getTimes();
                mSuggestedLocs = response.getLocations();
                if(isAJoiner)
                {
                    mJoinEventBtn.setText(getString(R.string.view_event_joiner_button));
                }

//                if(!response.getAllow_joiner_input() )
//                {
//                    mSuggestButtonsLayout.setVisibility(View.GONE);
//                }

                mAttendeesLabel.setText(getString(R.string.view_event_attendees_label) + " (max of " + response.getMax_attendees().intValue() + ")");
                addAttendees();
                addLocation();
                addTime();
            }
        }

        private void addAttendees()
        {
            //TODO: (Jobelle) View Event - Attendees Profile Pic
            for(int i=0; i < mAttendees.size(); i++) {
                View joiner = getLayoutInflater().inflate(R.layout.joiners_layout, mAttendeesList, false);
                LinearLayout joinersList = (LinearLayout) joiner.findViewById(R.id.joiner_layout);
                TextView joinerName = (TextView) joinersList.findViewById(R.id.joiner_name);
                joinerName.setText(mAttendees.get(i).getUsername());
                final UserProfile profile = mAttendees.get(i);

                //add on click listener
                joinersList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), ViewProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                        intent.putExtra("userName", profile.getUsername()); //add in the user name
                        intent.putExtra("userProfile", profile.getProfile_pic());
                        intent.putExtra("userId", profile.getUser_id());
                        intent.putExtra("favourited", profile.getFavourited());
                        intent.putStringArrayListExtra("userInterest", (ArrayList<String>)profile.getTagged_interests());
                        startActivity(intent);
                    }
                });

                mAttendeesList.addView(joinersList);
            }
        }

        private void addLocation()
        {
            Address addr;
            List<Address> addressList;
            int numOfVotes;

            for (int row = 0; row < 1; row++) {
                for (int i = 0; i < mSuggestedLocs.size(); i++)
                {
                    CheckBox addrCheckList = new CheckBox(getBaseContext());
                    addrCheckList.setTextColor(Color.BLACK);
                    addrCheckList.setId((row * 2) + i);
                    try {
                        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.CANADA);
                        addressList = geocoder.getFromLocation(mSuggestedLocs.get(i).getLatitude(),
                                mSuggestedLocs.get(i).getLongitude(), 1);
                        if (addressList != null && addressList.size() > 0) {
                            addr = addressList.get(0);

                            String addressText = String.format(
                                    "%s, %s, %s %s",
                                    // If there's a street address, add it
                                    addr.getMaxAddressLineIndex() > 0 ?
                                            addr.getAddressLine(0) : "",
                                    // Locality is usually a city
                                    addr.getLocality() != null ? addr.getLocality() : "",
                                    // The country of the address
                                    addr.getCountryName(),
                                    // If there's a postal code, add it
                                    addr.getPostalCode() != null ? addr.getPostalCode() : "");

                            //add votes
                            try {
                                numOfVotes = mSuggestedLocs.get(i).getSuggestion_votes().intValue();
                            } catch(NullPointerException e)
                            {
                                numOfVotes= -1;
                            }
                            addressText += "\n(" + numOfVotes + " vote(s))";

                            addrCheckList.setText(addressText);

                            mLocationList.addView(addrCheckList);
                        }
                    } catch (IOException e) {
                        Log.e("View Event", e.getMessage());
                    }
                }
            }
        }

        private void addTime()
        {
            for (int row = 0; row < 1; row++) {

                for (int i = 0; i < mSuggestedTimes.size(); i++) {
                    CheckBox timeCheckBox = new CheckBox(getBaseContext());
                    timeCheckBox.setTextColor(Color.BLACK);
                    timeCheckBox.setId((row * 2) + i);
                    timeCheckBox.setText(convertToTimeToString(mSuggestedTimes.get(i)));
                    mTimeList.addView(timeCheckBox);
                }
            }
        }

        private String convertToTimeToString(Time time)
        {
            long startDate = time.getStart().longValue();
            long endDate = time.getEnd().longValue();
            int numOfVotes;
            String s;

            //add votes
            try {
                numOfVotes = time.getSuggestion_votes().intValue();
            } catch (NullPointerException e)
            {
                numOfVotes = -1;
            }

            s = "     (" + numOfVotes + " vote(s))";

            Date d1 = new Date(startDate);
            Date d2 = new Date(endDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
            return "Start: " + dateFormat.format(d1) + s + "\nEnd  : " + dateFormat.format(d2);
        }
    }

    class JoinEventTask extends AsyncTask<Void, Void, String>
    {
        String sEventID, response;
        ProgressDialog progressDialog;
        ActsApi getEventApi;

        public JoinEventTask (String s)
        {
            sEventID = s;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog= ProgressDialog.show(ViewEventActivity.this, getString(R.string.join_event_progress_bar_title) + "...", getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            getEventApi = new ActsApi();
            getEventApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            getEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                getEventApi.joinActivity(sEventID);
                checkVotes();
            } catch (Exception e) {
                response = e.getMessage();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String message) {
            progressDialog.dismiss();
            message = "Joined Event";
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            finish();
        }

        private void checkVotes()
        {

            int addrCount = mLocationList.getChildCount();
            int timeCount = mTimeList.getChildCount();

            System.out.println("Count = " + addrCount);

            for (int i=0; i<addrCount; i++)
            {
                if(((CheckBox)mLocationList.getChildAt(i)).isChecked())
                {
                    try {
                        getEventApi.voteForSuggestion(mSuggestedLocs.get(i).getSuggestion_id());
                    } catch (Exception e) {
                        response = e.getMessage();
                    }
                }
            }

            System.out.println("Count = " + timeCount);

            for (int i=0; i<timeCount; i++)
            {
                if(((CheckBox)mTimeList.getChildAt(i)).isChecked())
                {
                    try {
                        getEventApi.voteForSuggestion(mSuggestedTimes.get(i).getSuggestion_id());
                    } catch (Exception e) {
                        response = e.getMessage();
                    }
                }
            }
        }
    }
}
