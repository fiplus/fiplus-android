package com.Fiplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import adapters.LocationArrayAdapterNoFilter;
import adapters.SuggestionListAdapter;
import model.SuggestionListItem;
import utils.DateTimePicker;
import utils.GeoAutoCompleteInterface;
import utils.GeocodingLocation;
import utils.IAppConstants;
import utils.PrefUtil;

public class ViewEventActivity extends FragmentActivity  implements TextWatcher, GeoAutoCompleteInterface {
    public static final String EXTRA_EVENT_ID = "eventID";

    protected TextView mEventDesc;
    protected AutoCompleteTextView mEventLocation;
    protected Button mJoinEventBtn;
    protected Button mCancelBtn;
    protected ListView mLocationList;
    protected ListView mTimeList;
    protected LinearLayout mAttendeesList;
    protected Button mSuggestTime;
    protected Button mAddLocationBtn;

    private SuggestionListAdapter mLocationListAdapter;
    private SuggestionListAdapter mTimesListAdapter;
    protected List<UserProfile> mAttendees = new ArrayList<UserProfile>();
    protected ArrayAdapter<String> autoCompleteLocationAdapter;

    boolean mIsAJoiner = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Bundle b = getIntent().getExtras();
        final String mEventID = b.getString(EXTRA_EVENT_ID);

        mEventDesc = (TextView) findViewById(R.id.view_event_description);
        mLocationList = (ListView) findViewById(R.id.view_event_loc_checkboxes);
        mTimeList = (ListView) findViewById(R.id.view_event_time_checkboxes);
        mAttendeesList = (LinearLayout)findViewById(R.id.view_event_attendees_list);

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
                if(mIsAJoiner)
                {
                    // un-join
                    UnJoinEventTask unJoinEventTask = new UnJoinEventTask(mEventID);
                    unJoinEventTask.execute();

                }
                finish();
            }
        });

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

        mEventLocation = (AutoCompleteTextView) findViewById(R.id.view_event_location);
        autoCompleteLocationAdapter = new LocationArrayAdapterNoFilter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteLocationAdapter.setNotifyOnChange(false);
        mEventLocation.addTextChangedListener(this);
        mEventLocation.setThreshold(MAX_CHARS);
        mEventLocation.setAdapter(autoCompleteLocationAdapter);


        mAddLocationBtn = (Button) findViewById(R.id.view_event_add_location);
        mAddLocationBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String address = mEventLocation.getText().toString();

                if(!address.isEmpty())
                {
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) && Geocoder.isPresent())
                    {
                        GeocodingLocation location = new GeocodingLocation(getBaseContext(), ViewEventActivity.this, FINAL_LOC);
                        location.execute(address);
                    }

                }
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        final String address = s.toString();

        if(!address.isEmpty() && address.length() >= MAX_CHARS)
        {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) && Geocoder.isPresent())
            {
                GeocodingLocation location = new GeocodingLocation(getBaseContext(), ViewEventActivity.this, AUTOCOMPLETE);
                location.execute(address);
            }
        }
        else
        {
            autoCompleteLocationAdapter.clear();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void populateLocation(Location location, String address) {
        //TODO Jobelle - add suggestion to list
    }

    @Override
    public ArrayAdapter<String> getAutoComplete() {
            return autoCompleteLocationAdapter;
    }

    class GetEventTask extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;
        Activity response;
        String sEventID, sEventDetails = "";
        Attendee attendees;

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
                            mIsAJoiner = true;
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
                setTitle(response.getName());
                String desc = response.getDescription();
                if(desc.length() == 0)
                {
                    mEventDesc.setVisibility(View.GONE);
                }
                else
                {
                    mEventDesc.setText(desc);
                }
                if(mIsAJoiner)
                {
                    mJoinEventBtn.setText(getString(R.string.view_event_joiner_button));
                    mCancelBtn.setText("Un-Join");
                }

//                if(!response.getAllow_joiner_input() )
//                {
//                    mSuggestButtonsLayout.setVisibility(View.GONE);
//                }

//                mAttendeesLabel.setText(getString(R.string.view_event_attendees_label) + " (max of " + response.getMax_attendees().intValue() + ")");
                addAttendees();
                addLocation(response.getLocations());
                addTime(response.getTimes());
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

        private void addLocation(List<Location> suggestedLocs)
        {
            ArrayList<SuggestionListItem> suggestionList = new ArrayList<SuggestionListItem>();
            Address addr;
            List<Address> addressList;
            int numOfVotes;

            for (int i = 0; i < suggestedLocs.size(); i++)
            {
                Location suggestion = suggestedLocs.get(i);
                try {
                    Geocoder geocoder = new Geocoder(getBaseContext(), Locale.CANADA);
                    addressList = geocoder.getFromLocation(suggestion.getLatitude(),
                            suggestion.getLongitude(), 1);

                    if (addressList != null && addressList.size() > 0) {
                        addr = addressList.get(0);
                        String addressText = String.format(
                                "%s, %s",
                                // If there's a street address, add it
                                addr.getMaxAddressLineIndex() > 0 ? addr.getAddressLine(0) : "",
                                // Locality is usually a city
                                addr.getLocality() != null ? addr.getLocality() : "");

                        //add votes
                        try {
                            numOfVotes = suggestion.getSuggestion_votes().intValue();
                        } catch(NullPointerException e)
                        {
                            numOfVotes = -1;
                        }

                        boolean yesVote = suggestion.getSuggestion_voters().contains(
                                PrefUtil.getString(getApplicationContext(), IAppConstants.USER_ID, null));
                        suggestionList.add(new SuggestionListItem(suggestion.getSuggestion_id(),
                                addressText, numOfVotes, yesVote));
                    }
                } catch (IOException e) {
                    Log.e("View Event", e.getMessage());
                }
            }

            mLocationListAdapter = new SuggestionListAdapter(ViewEventActivity.this, suggestionList);
            mLocationList.setAdapter(mLocationListAdapter);
        }

        private void addTime(List<Time> times)
        {
            ArrayList<SuggestionListItem> suggestionList = new ArrayList<SuggestionListItem>();
            for (int i = 0; i < times.size(); i++) {
                Time time = times.get(i);

                int numOfVotes;
                //add votes
                try {
                    numOfVotes = time.getSuggestion_votes().intValue();
                } catch(NullPointerException e)
                {
                    numOfVotes = -1;
                }

                boolean yesVote = time.getSuggestion_voters().contains(
                        PrefUtil.getString(getApplicationContext(), IAppConstants.USER_ID, null));
                suggestionList.add(new SuggestionListItem(time.getSuggestion_id(),
                        convertToTimeToString(time), numOfVotes, yesVote));
            }

            mTimesListAdapter = new SuggestionListAdapter(ViewEventActivity.this, suggestionList);
            mTimeList.setAdapter(mTimesListAdapter);
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

            s = " (" + numOfVotes + " vote(s))";

            Date d1 = new Date(startDate);
            Date d2 = new Date(endDate);
            String format = "EEE MMM d, h:m a";

            String format2 = format;
            String middle = " to ";
            boolean curYear = d1.getYear() == new Date().getYear();
            boolean sameMonth = d1.getMonth() == d2.getMonth();
            boolean sameDay = d1.getDate() == d2.getDate();

            if(sameDay && sameMonth)
            {
                format2 = "h:m a";
                middle = " to ";
            }
            if(!curYear)
            {
                format2 += ", yyyy";
            }

            SimpleDateFormat sdf1 = new SimpleDateFormat(format);
            SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
            return sdf1.format(d1) + middle + sdf2.format(d2);
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

            for (int i=0; i<addrCount; i++)
            {
                if(((CheckBox)mLocationList.getChildAt(i)
                        .findViewById(R.id.suggestion_checkbox)).isChecked())
                {
                    try {
                        getEventApi.voteForSuggestion(mLocationListAdapter.getItem(i).getSuggestionId());
                    } catch (Exception e) {
                        response = e.getMessage();
                    }
                }
                else
                {
                    try {
                        getEventApi.unvoteForSuggestion(mLocationListAdapter.getItem(i).getSuggestionId());
                    } catch (Exception e) {
                        response = e.getMessage();
                    }
                }
            }

            for (int i=0; i<timeCount; i++)
            {
                if(((CheckBox)mTimeList.getChildAt(i)
                        .findViewById(R.id.suggestion_checkbox)).isChecked())
                {
                    try {
                        getEventApi.voteForSuggestion(mTimesListAdapter.getItem(i).getSuggestionId());
                    } catch (Exception e) {
                        response = e.getMessage();
                    }
                }
                else
                {
                    try {
                        getEventApi.unvoteForSuggestion(mTimesListAdapter.getItem(i).getSuggestionId());
                    } catch (Exception e) {
                        response = e.getMessage();
                    }
                }
            }
        }
    }


    class UnJoinEventTask extends AsyncTask<Void, Void, String>
    {
        String sEventID, response;
        ProgressDialog progressDialog;
        ActsApi getEventApi;

        public UnJoinEventTask (String s)
        {
            sEventID = s;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(ViewEventActivity.this,
                    getString(R.string.unjoin_event_progress_bar_title) + "...",
                    getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            getEventApi = new ActsApi();
            getEventApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            getEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                getEventApi.unjoinActivity(sEventID);
            } catch (Exception e) {
                response = e.getMessage();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String message) {
            progressDialog.dismiss();
            message = "Un-Joined Event";
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
