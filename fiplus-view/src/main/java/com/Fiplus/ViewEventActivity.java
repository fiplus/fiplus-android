package com.Fiplus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
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

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wordnik.client.api.ActsApi;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Activity;
import com.wordnik.client.model.Attendee;
import com.wordnik.client.model.CreateSuggestionResponse;
import com.wordnik.client.model.Joiner;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.Time;
import com.wordnik.client.model.UserProfile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import adapters.LocationArrayAdapterNoFilter;
import adapters.PendingLocListAdapter;
import adapters.PendingTimeLocListAdapter;
import adapters.SuggestionListAdapter;
import model.PendingLocItem;
import model.PendingTimeItem;
import model.SuggestionListItem;
import utils.DateTimePicker;
import utils.FirmUpDialog;
import utils.GeoAutoCompleteInterface;
import utils.GeocodingLocation;
import utils.IAppConstants;
import utils.ListViewUtil;
import utils.LocationUtil;
import utils.PrefUtil;

public class ViewEventActivity extends FragmentActivity  implements TextWatcher, GeoAutoCompleteInterface {
    public static final String EXTRA_EVENT_ID = "eventID";

    protected TextView mEventDesc;
    protected AutoCompleteTextView mEventLocation;
    protected Button mJoinEventBtn;
    protected Button mCancelBtn;
    protected ListView mLocationList;
    protected ListView mTimeList;
    protected ListView mSuggestedLocList;
    protected ListView mSuggestedTimeList;
    protected LinearLayout mAttendeesList;
    protected Button mSuggestTimeBtn;
    protected Button mAddLocationBtn;
    protected TextView mLocationLabel;
    protected TextView mTimeLabel;

    ArrayList<PendingLocItem> locPendingSuggestion = new ArrayList<PendingLocItem>();
    private PendingLocListAdapter mPendingLocSuggestionAdapter;

    ArrayList<PendingTimeItem> timePendingSuggestion = new ArrayList<PendingTimeItem>();
    private PendingTimeLocListAdapter mPendingTimeSuggestionAdapter;

    ArrayList<SuggestionListItem> timeSuggestionList = new ArrayList<SuggestionListItem>();
    private SuggestionListAdapter mTimesListAdapter;
    protected List<UserProfile> mAttendees = new ArrayList<UserProfile>();

    ArrayList<SuggestionListItem> locSuggestionList = new ArrayList<SuggestionListItem>();
    protected ArrayAdapter<String> autoCompleteLocationAdapter;
    private SuggestionListAdapter mLocationListAdapter;

    boolean mIsAJoiner = false;
    boolean mIsACreator = false;
    boolean mIsCanceled = false;
    boolean mIsConfirmed = false;
    boolean mNeedRSVP = false;
    String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Tracker t = ((FiplusApplication)this.getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Bundle b = getIntent().getExtras();
        final String mEventID = b.getString(EXTRA_EVENT_ID);
        eventID = mEventID;

        t.send(new HitBuilders.EventBuilder().setCategory(FiplusApplication.VIEWS_CATEGORY)
                .setAction(FiplusApplication.VIEWED_EVENT_ACTION)
                .setLabel(eventID).build());

        if(b.getBoolean(GcmMessageProcessor.FROM_NOTIFICATION))
        {
            t.send(new HitBuilders.EventBuilder().setCategory(FiplusApplication.VIEWS_CATEGORY)
                .setAction(FiplusApplication.CLICKED_NOTIFICATION_ACTION).build());
        }

        mEventDesc = (TextView) findViewById(R.id.view_event_description);
        mLocationList = (ListView) findViewById(R.id.view_event_loc_checkboxes);
        mLocationList.setOnTouchListener(new TouchListener());
        mLocationLabel = (TextView) findViewById(R.id.view_event_loc_label);
        mTimeLabel = (TextView) findViewById(R.id.view_event_time_label);

        mSuggestedLocList = (ListView) findViewById(R.id.view_event_pending_loc);
        mPendingLocSuggestionAdapter = new PendingLocListAdapter(this, locPendingSuggestion, mSuggestedLocList);
        mSuggestedLocList.setAdapter(mPendingLocSuggestionAdapter);
        mSuggestedLocList.setOnTouchListener(new TouchListener());

        mSuggestedTimeList = (ListView) findViewById(R.id.view_event_pending_time);
        mPendingTimeSuggestionAdapter = new PendingTimeLocListAdapter(this, timePendingSuggestion, mSuggestedTimeList);
        mSuggestedTimeList.setAdapter(mPendingTimeSuggestionAdapter);
        mSuggestedTimeList.setOnTouchListener(new TouchListener());

        mTimeList = (ListView) findViewById(R.id.view_event_time_checkboxes);
        mTimeList.setOnTouchListener(new TouchListener());

        mAttendeesList = (LinearLayout)findViewById(R.id.view_event_attendees_list);
        mSuggestedTimeList.setOnTouchListener(new TouchListener());

        GetEventTask getEventTask = new GetEventTask(mEventID);
        getEventTask.execute();

        mJoinEventBtn = (Button) findViewById(R.id.view_event_join_btn);
        mJoinEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Invalidate the my events cache to get updated values
                PrefUtil.putBoolean(getApplicationContext(), IAppConstants.MY_EVENTS_CACHE_VALID_FLAG, false);

                if(mIsACreator)
                {
                    FirmUpDialog firmUp = new FirmUpDialog(ViewEventActivity.this, eventID, locSuggestionList, timeSuggestionList, mIsACreator);
                    firmUp.showFirmUpRsvp();


                }
                else
                {
                    JoinEventTask joinEventTask = new JoinEventTask(mEventID);
                    joinEventTask.execute();
                }

            }
        });

        //TODO: (Jobelle) Join Event - Change button label based on user (i.e. isCreator or isJoiner)
        mCancelBtn = (Button) findViewById(R.id.view_event_not_interested_btn);
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsACreator)
                {
                    CancelEventTask cancelEventTask = new CancelEventTask(mEventID);
                    cancelEventTask.execute();
                }
                if(mIsAJoiner)
                {
                    UnJoinEventTask unJoinEventTask = new UnJoinEventTask(mEventID);
                    unJoinEventTask.execute();
                }
                // Invalidate the my events cache to get updated values
                PrefUtil.putBoolean(getApplicationContext(), IAppConstants.MY_EVENTS_CACHE_VALID_FLAG, false);
                finish();
            }
        });

        mSuggestTimeBtn = (Button) findViewById(R.id.view_event_suggest_time);
        mSuggestTimeBtn.setOnClickListener(new View.OnClickListener()
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
        //update votes
        if((mIsAJoiner || mIsACreator) && !mIsConfirmed)
        {
            UpdateVotes update = new UpdateVotes();
            update.execute();
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
    }

    @Override
    public void onPause() {
        //update votes
        if((mIsAJoiner || mIsACreator) && !mIsConfirmed)
        {
            UpdateVotes update = new UpdateVotes();
            update.execute();
        }

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

        if(address!=null)
        {
            mEventLocation.setText("");
            //if the user is the creator/joiner of an event, automatically add
            //the suggested location
            if(mIsACreator || mIsAJoiner)
            {
                AddSuggestionTask newSuggest = new AddSuggestionTask(location, address);
                newSuggest.execute();
            }
            else //put on pending list first
            {
                locPendingSuggestion.add(new PendingLocItem(address, location));
                mPendingLocSuggestionAdapter.notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(mSuggestedLocList);

                if(locPendingSuggestion.size() > 0) {
                    mSuggestedLocList.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            mEventLocation.setError(getString(R.string.error_address_not_found));
        }
    }

    @Override
    public ArrayAdapter<String> getAutoComplete() {
            return autoCompleteLocationAdapter;
    }

    //gets called from DateTimePicker
    public void addDateTime(Time time, String sTime)
    {
        //if the user is the creator/joiner of an event, automatically add
        //the suggested time
        if(mIsAJoiner || mIsACreator)
        {
            AddSuggestionTask newSuggest = new AddSuggestionTask(time);
            newSuggest.execute();
        }
        else //add to pending list first
        {
            timePendingSuggestion.add(new PendingTimeItem(convertToTimeToString(time), time));
            mPendingTimeSuggestionAdapter.notifyDataSetChanged();
            ListViewUtil.setListViewHeightBasedOnChildren(mSuggestedTimeList);

            if(timePendingSuggestion.size() > 0) {
                mSuggestedTimeList.setVisibility(View.VISIBLE);
            }
        }
    }

    private String convertToTimeToString(Time time)
    {
        String format = "E MMM dd, hh:mm a";
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


        long startDate = time.getStart().longValue();
        if(time.getEnd() == null)
        {
            time.setEnd(time.getStart());
        }

        long endDate= time.getEnd().longValue();

        Date d1 = new Date(startDate);
        Date d2 = new Date(endDate);

        String format2 = format;
        String middle = " to ";
        boolean curYear = d1.getYear() == new Date().getYear();
        boolean sameMonth = d1.getMonth() == d2.getMonth();
        boolean sameDay = d1.getDate() == d2.getDate();

        if(sameDay && sameMonth)
        {
            format2 = "hh:mm a";
            middle = " to ";
        }
        if(!curYear)
        {
            format2 += ", yyyy";
        }

        SimpleDateFormat sdf1 = new SimpleDateFormat(format);
        SimpleDateFormat sdf2 = new SimpleDateFormat(format2);

        if(startDate == endDate)
        {
            return sdf1.format(d1);
        }
        else
        {
            return sdf1.format(d1) + middle + sdf2.format(d2);
        }
    }

    //To handle multiple scrollviews
    protected class TouchListener implements ListView.OnTouchListener {
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
    }

    class GetEventTask extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;
        Activity response;
        String sEventID, creator, sEventDetails = "";
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
            getEventApi.getInvoker().setContext(getBaseContext());
            getEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                response = getEventApi.getActivity(sEventID);
                sEventDetails = response.toString();
                attendees = getEventApi.getAttendees(sEventID, null);
                creator = response.getCreator();
                mIsCanceled = response.getIs_cancelled();
                Log.e("Cancelled Event", String.valueOf(mIsCanceled));

                mIsConfirmed = response.getIs_confirmed();
                Log.e("Confirmed Event", String.valueOf(mIsConfirmed));
            } catch (Exception e) {
                sEventDetails = e.getMessage();
                Log.e("Error - Get Activity", sEventDetails);
            }

            UsersApi usersApi = new UsersApi();
            usersApi.getInvoker().setContext(getBaseContext());
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);
            UserProfile sUserProfile;

            if(creator.equalsIgnoreCase(PrefUtil.getString(getApplicationContext(), IAppConstants.USER_ID, null)))
            {
                mIsACreator = true;

                Log.e("Creator?", String.valueOf(mIsACreator));
            }
            // to handle bad events
            try
            {
                for(int i=0; i < attendees.getJoiners().size(); i++)
                {
                    try {
                        Joiner joiner = attendees.getJoiners().get(i);
                        //check if a user is a joiner of this event
                        if(joiner.getJoiner_id().equalsIgnoreCase(PrefUtil.getString(getApplicationContext(), IAppConstants.USER_ID, null)))
                        {
                            mIsAJoiner = true;
                            Log.e("Joiner?", String.valueOf(mIsAJoiner));

                            mNeedRSVP = joiner.getConfirmed();
                            Log.e("Need RSVP?", String.valueOf(mNeedRSVP));
                        }
                        sUserProfile = usersApi.getUserProfile(joiner.getJoiner_id());
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
                String desc = response.getDescription();
                if(desc.length() == 0)
                {
                    mEventDesc.setVisibility(View.GONE);
                }
                else
                {
                    mEventDesc.setText(desc);
                }

                if(mIsCanceled)
                {
                    setTitle(response.getName() + " - CANCELLED");
                    mJoinEventBtn.setVisibility(View.GONE);
                    mCancelBtn.setVisibility(View.GONE);
                    mEventLocation.setVisibility(View.GONE);
                    mSuggestTimeBtn.setVisibility(View.GONE);
                    mAddLocationBtn.setVisibility(View.GONE);
                }
                else
                {
                    setView();
                }

                addAttendees();
                addLocation(response.getLocations());
                addTime(response.getTimes());
            }
        }

        private void setView()
        {
            setTitle(response.getName());

            if(mIsACreator)
            {
                mJoinEventBtn.setText(getString(R.string.view_event_firm_up_button));
                mCancelBtn.setText("Cancel Event");

                if(mIsConfirmed)
                {
                    mLocationLabel.setText("Location:");
                    mTimeLabel.setText("Time:");

                    mJoinEventBtn.setVisibility(View.GONE);
                    mEventLocation.setVisibility(View.GONE);
                    mSuggestTimeBtn.setVisibility(View.GONE);
                    mAddLocationBtn.setVisibility(View.GONE);
                }

            }
            else if(mIsAJoiner)
            {
                mJoinEventBtn.setVisibility(View.GONE);
                //mJoinEventBtn.setText(getString(R.string.view_event_joiner_button));
                mCancelBtn.setText("Un-Join");
            }

            if(mIsConfirmed)
            {
                mLocationLabel.setText("Location:");
                mTimeLabel.setText("Time:");

                mEventLocation.setVisibility(View.GONE);
                mSuggestTimeBtn.setVisibility(View.GONE);
                mAddLocationBtn.setVisibility(View.GONE);

                if(mIsAJoiner)
                {
                    if(mNeedRSVP)
                    {
                        mJoinEventBtn.setVisibility(View.VISIBLE);
                        mJoinEventBtn.setText(getString(R.string.view_event_rsvp_button));
                    }
                    else
                    {
                        mJoinEventBtn.setVisibility(View.GONE);
                    }
                }
            }

            //Hide the suggest buttons if the creator does not allow user input
            //and if the user is not a joiner
            if(!response.getAllow_joiner_input() && !mIsACreator)
            {
                mEventLocation.setVisibility(View.GONE);
                mSuggestTimeBtn.setVisibility(View.GONE);
                mAddLocationBtn.setVisibility(View.GONE);
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
            Address addr;
            List<Address> addressList;
            int numOfVotes;

            for (int i = 0; i < suggestedLocs.size(); i++)
            {
                Location suggestion = suggestedLocs.get(i);
                //add votes
                try {
                    numOfVotes = suggestion.getSuggestion_votes().intValue();
                } catch(NullPointerException e)
                {
                    numOfVotes = -1;
                }

                boolean yesVote = suggestion.getSuggestion_voters().contains(
                        PrefUtil.getString(getApplicationContext(), IAppConstants.USER_ID, null));
                locSuggestionList.add(new SuggestionListItem(suggestion.getSuggestion_id(),
                        LocationUtil.getLocationString(suggestion, getBaseContext()),
                        numOfVotes, yesVote));
            }

            mLocationListAdapter = new SuggestionListAdapter(ViewEventActivity.this, locSuggestionList, mIsCanceled, mIsConfirmed);
            mLocationList.setAdapter(mLocationListAdapter);
            ListViewUtil.setListViewHeightBasedOnChildren(mLocationList);

            mLocationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    boolean isChecked = locSuggestionList.get(position).getYesVote();
                    locSuggestionList.get(position).setYesVote(!isChecked);
                    if (isChecked) //if true, then deduct 1 coz we unchecked
                    {
                        locSuggestionList.get(position).setVote(locSuggestionList.get(position).getVote() - 1);
                    } else //add 1 coz we checked
                    {
                        locSuggestionList.get(position).setVote(locSuggestionList.get(position).getVote() + 1);
                    }
                    mLocationListAdapter.notifyDataSetChanged();

                }
            });

            if(mIsCanceled) //change color
            {
                mLocationLabel.setTextColor(Color.GRAY);
            }
        }

        private void addTime(List<Time> times)
        {

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
                timeSuggestionList.add(new SuggestionListItem(time.getSuggestion_id(),
                        convertToTimeToString(time), numOfVotes, yesVote));
            }

            mTimesListAdapter = new SuggestionListAdapter(ViewEventActivity.this, timeSuggestionList, mIsCanceled, mIsConfirmed);
            mTimeList.setAdapter(mTimesListAdapter);
            ListViewUtil.setListViewHeightBasedOnChildren(mTimeList);

            mTimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    boolean isChecked = timeSuggestionList.get(position).getYesVote();
                    timeSuggestionList.get(position).setYesVote(!isChecked);
                    if(isChecked) //if true, then deduct 1 coz we unchecked
                    {
                        timeSuggestionList.get(position).setVote(timeSuggestionList.get(position).getVote()-1);
                    }
                    else //add 1 coz we checked
                    {
                        timeSuggestionList.get(position).setVote(timeSuggestionList.get(position).getVote()+1);
                    }
                    mTimesListAdapter.notifyDataSetChanged();

                }
            });

            if(mIsCanceled) //change color
            {
                mTimeLabel.setTextColor(Color.GRAY);
            }
        }

    }

    class JoinEventTask extends AsyncTask<Void, Void, String>
    {
        String sEventID, response = "";
        ProgressDialog progressDialog;
        ActsApi getEventApi;
        boolean error = false;

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
            getEventApi.getInvoker().setContext(getBaseContext());
            getEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                getEventApi.joinActivity(sEventID);
                response = checkPendingSuggestions();
                response = checkVotes();
            } catch (Exception e) {
                error = true;
                response = e.getMessage();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String message) {
            progressDialog.dismiss();
            Log.e("Join", message);

            if(error) // have a pop up
            {
                message = message.replace("\"", "");
                message = message.replace("{error:", "");
                message = message.replace("}", "");

                if(message.toLowerCase().contains("duplicate"))
                {
                    if(message.toLowerCase().contains("location"))
                    {
                        message = "One or more of your suggested locations were ignored. Duplicate suggestions are not allowed.";
                    }
                    else if(message.toLowerCase().contains("time"))
                    {
                        message = "One or more of your suggested times were ignored. Duplicate suggestions are not allowed.";
                    }
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewEventActivity.this);
                alertDialog.setTitle("Warning").setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        joinSuccessful();
                    }
                });
                alertDialog.show();

                //Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
            else
            {
                joinSuccessful();
            }
        }

        private void joinSuccessful()
        {
            String message;

            if(mIsAJoiner)
            {
                message = "Event updated";
            }
            else
            {
                message = "Joined Event!";
            }

            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            finish();
        }

        private String checkPendingSuggestions()
        {
            int pendingLocCount = mSuggestedLocList.getCount();
            int pendingTimeCount = mSuggestedTimeList.getCount();

            for(int i = 0; i < pendingLocCount; i++)
            {
                try
                {
                    getEventApi.suggestLocationForActivity(sEventID, mPendingLocSuggestionAdapter.getItem(i).getLoc());
                }
                catch (Exception e) {
                    error = true;
                    response = e.getMessage() + "pendingLoc";
                }
            }

            for(int i = 0; i < pendingTimeCount; i++)
            {
                try
                {
                    getEventApi.suggestTimeForActivity(sEventID, mPendingTimeSuggestionAdapter.getItem(i).getTime());
                }
                catch (Exception e) {
                    error = true;
                    response = e.getMessage() + "pendingTime";
                }
            }

            return response;
        }

        private String checkVotes()
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
                        error = true;
                        response = e.getMessage();
                    }
                }
                else
                {
                    try {
                        getEventApi.unvoteForSuggestion(mLocationListAdapter.getItem(i).getSuggestionId());
                    } catch (Exception e) {
                        error = true;
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
                        error = true;
                        response = e.getMessage();
                    }
                }
                else
                {
                    try {
                        getEventApi.unvoteForSuggestion(mTimesListAdapter.getItem(i).getSuggestionId());
                    } catch (Exception e) {
                        error = true;
                        response = e.getMessage();
                    }
                }
            }

            return response;
        }
    }

    class UnJoinEventTask extends AsyncTask<Void, Void, String>
    {
        String sEventID, response = null;
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
            getEventApi.getInvoker().setContext(getBaseContext());
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

            if(message==null)
            {
                message = "Un-Joined Event";
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Log.e("Unjoin", message);
            }

            finish();
        }
    }

    class CancelEventTask extends AsyncTask<Void, Void, String>
    {
        String sEventID, response;
        ProgressDialog progressDialog;
        ActsApi getEventApi;

        public CancelEventTask (String s)
        {
            sEventID = s;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(ViewEventActivity.this,
                    getString(R.string.cancel_event_progress_bar_title) + "...",
                    getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            getEventApi = new ActsApi();
            getEventApi.getInvoker().setContext(getBaseContext());
            getEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);
            try {
                getEventApi.cancelActivity(sEventID);
            } catch (Exception e) {
                response = e.getMessage();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String message) {
            progressDialog.dismiss();
            message = "Cancelled Event";
            Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    class AddSuggestionTask extends AsyncTask<Void, Void, String>
    {
        boolean error = false;
        ActsApi setEventApi;
        String sEventID = eventID, response = "";
        Time suggestedTime = null;
        Location suggestedLocation = null;
        String address;
        boolean isTime;
        CreateSuggestionResponse suggested;

        public AddSuggestionTask(Time t)
        {
            suggestedTime = t;
            isTime = true;
        }

        public AddSuggestionTask(Location loc, String s)
        {
            suggestedLocation = loc;
            address = s;
            isTime = false;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            setEventApi = new ActsApi();
            setEventApi.getInvoker().setContext(getBaseContext());
            setEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {

                if(isTime)
                {
                    suggested = setEventApi.suggestTimeForActivity(sEventID, suggestedTime);
                }
                else
                {
                    suggested = setEventApi.suggestLocationForActivity(sEventID, suggestedLocation);
                }



            } catch (Exception e) {
                error = true;
                response = e.getMessage();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String message) {
            Log.e("Suggestion", message);

            if(error)
            {
                message = message.replace("\"", "");
                message = message.replace("{error:", "");
                message = message.replace("}", "");

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewEventActivity.this);
                alertDialog.setTitle("Error").setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
            else
            {
                if(isTime)
                {
                    timeSuggestionList.add(new SuggestionListItem(suggested.getSuggestion_id(),
                            convertToTimeToString(suggestedTime), 1, true));
                    mTimesListAdapter.notifyDataSetChanged();
                    ListViewUtil.setListViewHeightBasedOnChildren(mTimeList);
                }
                else
                {
                    locSuggestionList.add(new SuggestionListItem(suggested.getSuggestion_id(),
                            address, 1, true));
                    mLocationListAdapter.notifyDataSetChanged();
                    ListViewUtil.setListViewHeightBasedOnChildren(mLocationList);
                }
            }

        }
    }

    class UpdateVotes extends AsyncTask<Void, Void, String>
    {
        ActsApi getEventApi;
        String response = null;

        @Override
        protected String doInBackground(Void... params)
        {
            getEventApi = new ActsApi();
            getEventApi.getInvoker().setContext(getBaseContext());
            getEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {

                response = checkVotes();

            } catch (Exception e) {
                response = e.getMessage();
            }

            return response;
        }

        private String checkVotes()
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

            return response;
        }

        @Override
        protected void onPostExecute(String message) {

            if(message != null) // have a pop up
            {
                message = message.replace("\"", "");
                message = message.replace("{error:", "");
                message = message.replace("}", "");

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewEventActivity.this);
                alertDialog.setTitle("Warning").setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        }

    }
}
