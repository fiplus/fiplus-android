package com.Fiplus;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wordnik.client.ApiException;
import com.wordnik.client.api.ActsApi;
import com.wordnik.client.api.InterestsApi;
import com.wordnik.client.model.Activity;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.Time;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import adapters.LocationArrayAdapterNoFilter;
import adapters.RemovableItemAdapter;
import utils.DateTimePicker;
import utils.GeoAutoCompleteInterface;
import utils.GeocodingLocation;
import utils.IAppConstants;
import utils.ListViewUtil;
import utils.PrefUtil;


public class CreateEventActivity extends FragmentActivity implements TextWatcher, GeoAutoCompleteInterface {
    final String btnInnerHTML = "<font color='gray'>\"%s\"    </font>";

    //protected ImageView mImageView;
    protected EditText mEventName;
    protected AutoCompleteTextView mEventLocation;
    protected Button mAddLocationBtn;
    protected ListView mLocationListView;
    protected EditText mDescription;
    protected Button mCreateButton;
    protected Button mCancelButton;
    protected EditText mMaxPeople;
    protected CheckBox mAllowSuggestions;
    protected Button mDateTimeButton;
    protected ListView mDateTimeListView;
    protected AutoCompleteTextView mTags;
    protected TextView mAddTags;
    protected ArrayAdapter<String> autoCompleteInterestAdapter;
    protected List<String> interestsList;
    protected RemovableItemAdapter mRemovableLocationAdapter;
    protected RemovableItemAdapter mRemovableDateTimeAdapter;


    protected EditText mDateTimeError;

    protected List<Location> mEventLocationList = new ArrayList<Location>();
    protected List<String> mEventLocationListItems = new ArrayList<String>();
    protected ArrayAdapter<String> autoCompleteLocationAdapter;

    protected List<Time> mDateTimeListItemsUTC = new ArrayList<Time>();
    protected List<String> mDateTimeListItems = new ArrayList<String>();

    protected List<String> mTagsList = new ArrayList<String>();
    protected FlowLayout mTagsLinearLayout;

    //zero argument constructor
    public CreateEventActivity()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tracker t = ((FiplusApplication)this.getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        setTitle(R.string.create_event_activity_title);

        mEventName = (EditText) findViewById(R.id.create_event_name);
        mDescription = (EditText) findViewById(R.id.create_event_description);

        mEventLocation = (AutoCompleteTextView) findViewById(R.id.create_event_location);
        autoCompleteLocationAdapter = new LocationArrayAdapterNoFilter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteLocationAdapter.setNotifyOnChange(false);
        mEventLocation.addTextChangedListener(this);
        mEventLocation.setThreshold(MAX_CHARS);
        mEventLocation.setAdapter(autoCompleteLocationAdapter);


        mAddLocationBtn = (Button) findViewById(R.id.create_event_add_location);
        mAddLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = mEventLocation.getText().toString();

                if (!address.isEmpty()) {
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) && Geocoder.isPresent()) {
                        GeocodingLocation location = new GeocodingLocation(getBaseContext(), CreateEventActivity.this, FINAL_LOC);
                        location.execute(address);
                    }

                }
            }
        });

        //to show the list of suggested locations
        mLocationListView = (ListView)findViewById(R.id.create_event_address_list);
        mRemovableLocationAdapter = new RemovableItemAdapter(this, mEventLocationListItems, mEventLocationList, mLocationListView);
        mLocationListView.setAdapter(mRemovableLocationAdapter);
        mLocationListView.setOnTouchListener(new TouchListener());
        ListViewUtil.setListViewHeightBasedOnChildren(mLocationListView);

        mMaxPeople = (EditText) findViewById(R.id.create_event_number_of_people);
        mAllowSuggestions = (CheckBox) findViewById(R.id.create_event_suggestion_checkbox);

        //for tags
        mTags = (AutoCompleteTextView) findViewById(R.id.create_event_tags);
        mTags.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mTags.setThreshold(1);
        mAddTags = (TextView) findViewById(R.id.create_event_tags_label);
        mTagsLinearLayout = (FlowLayout) findViewById(R.id.create_event_tags_list);

        mDateTimeError = (EditText) findViewById(R.id.create_event_datetime_error);
        mDateTimeButton = (Button) findViewById(R.id.create_event_add_datetime);
        mDateTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDateTimeError.setError(null);
                DateTimePicker getDateTime = new DateTimePicker(CreateEventActivity.this);
                getDateTime.showDateTimePickerDialog();
            }
        });

        //to show the list of suggested start and end date/time
        mDateTimeListView = (ListView)findViewById(R.id.create_event_datetimelist);
        mRemovableDateTimeAdapter  = new RemovableItemAdapter(this, mDateTimeListItems, mDateTimeListView, mDateTimeListItemsUTC);
        mDateTimeListView.setAdapter(mRemovableDateTimeAdapter);
        mDateTimeListView.setOnTouchListener(new TouchListener());

        ListViewUtil.setListViewHeightBasedOnChildren(mDateTimeListView);

        mCreateButton = (Button) findViewById(R.id.create_event_create__button);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMandatoryFields();
            }
        });

        mCancelButton = (Button) findViewById(R.id.create_event_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GetInterestsTask getInterestsTask = new GetInterestsTask();
        getInterestsTask.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_in_from_top, R.anim.activity_out_to_bottom);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_in_from_top, R.anim.activity_out_to_bottom);
    }

    private void checkMandatoryFields()
    {
        //View focusView = mEventName;
        boolean error = false;

        // Reset errors.
        mEventName.setError(null);
        mMaxPeople.setError(null);
        mDateTimeError.setError(null);

        // Store values at the time of the create event attempt.
        String event_name = mEventName.getText().toString();
        String maxPeople = mMaxPeople.getText().toString();

        //Check for mandatory field
        if (TextUtils.isEmpty(event_name)) {
            mEventName.setError(getString(R.string.error_field_required));
            error = true;
        }

        if (mEventLocationListItems.isEmpty())
        {
            mEventLocation.setError(getString(R.string.error_add_address));
            error = true;
        }

        if (mDateTimeListItems.isEmpty())
        {
            mDateTimeError.setError(getString(R.string.error_field_required));
            error = true;
        }

        if (TextUtils.isEmpty(maxPeople)) {
            mMaxPeople.setError(getString(R.string.error_field_required));
            error = true;
        }
        else if (Integer.parseInt(maxPeople) < 2) {
            mMaxPeople.setError(getString(R.string.error_max_people));
            error = true;
        }


        // If there was an error; don't attempt to create event and focus the first
        // form field with an error.
//        if (error) {
//            focusView.requestFocus();
//        }
        if(!error)
        {
            CreateEventTask createEventTask = new CreateEventTask();
            createEventTask.execute();
        }
    }

    //gets called from DateTimePicker
    public void addDateTime(Time time, String sTime)
    {
        //getDateTime.convertTimeToString(time)
        mDateTimeListItems.add(sTime);
        mDateTimeListItemsUTC.add(time);
        mRemovableDateTimeAdapter.notifyDataSetChanged();
        ListViewUtil.setListViewHeightBasedOnChildren(mDateTimeListView);

        if(mDateTimeListItems.size() > 0) {
            mDateTimeListView.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * This is for text listener in the address textbox
     */

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

        mEventLocation.setError(null);
        final String address = arg0.toString();

        if(!address.isEmpty() && address.length() >= MAX_CHARS)
        {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) && Geocoder.isPresent())
            {
                GeocodingLocation location = new GeocodingLocation(getBaseContext(), CreateEventActivity.this, AUTOCOMPLETE);
                location.execute(address);
            }
        }
        else
        {
            autoCompleteLocationAdapter.clear();
        }
    }

    @Override
    public void afterTextChanged(Editable arg0) {
    }

    public ArrayAdapter<String> getAutoComplete()
    {
        return autoCompleteLocationAdapter;
    }

    public void populateLocation(Location location, String address)
    {

        // Display the results of the lookup.
        if(address != null)
        {
            mEventLocationList.add(location);
            mEventLocationListItems.add(address);
            mRemovableLocationAdapter.notifyDataSetChanged();
            ListViewUtil.setListViewHeightBasedOnChildren(mLocationListView);

            if(mEventLocationListItems.size() > 0) {
                mLocationListView.setVisibility(View.VISIBLE);
            }

            mEventLocation.setText("");
        }
        else
        {
            mEventLocation.setError(getString(R.string.error_address_not_found));
        }
    }

    //Add event tags
    public void onAddTagsClick(View view)
    {
        String tag;
        tag = mTags.getText().toString();
        int tagsAdded;
        mTags.setText("");

        if(!tag.isEmpty())
        {
            mTagsList.add(tag);
            tagsAdded = mTagsList.size();

            final TextView createEventTag = new TextView(getBaseContext());
            createEventTag.setText(Html.fromHtml(String.format(btnInnerHTML, mTagsList.get(tagsAdded - 1))));
            createEventTag.setBackgroundResource(R.drawable.button_tags);
            createEventTag.setTextColor(Color.BLACK);
            createEventTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tags_delete, 0);

            createEventTag.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= createEventTag.getRight()) {
                            removeTag(v, createEventTag.getText().toString());
                        }
                    }
                    return true;
                }
            });

            int padding = getResources().getDimensionPixelOffset(R.dimen.tags_padding);
            createEventTag.setPadding(padding, 0, padding, 0);

            mTagsLinearLayout.addView(createEventTag);
        }

    }

    //to remove tag and update the list
    public void removeTag(View v, String tag)
    {
        v.setVisibility(View.GONE);
        tag = tag.replace("\"", "");
        tag = tag.replace(" ", "");

        for(int i = 0; i < mTagsList.size(); i++) {
            if (mTagsList.get(i).contains(tag)) {
                mTagsList.remove(i);
                break;
            }
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

    class GetInterestsTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params) {

            InterestsApi interests = new InterestsApi();
            interests.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            interests.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                interestsList = interests.getInterestsWithInput(null);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            autoCompleteInterestAdapter = new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_list_item_1, interestsList);
            autoCompleteInterestAdapter.setNotifyOnChange(false);
            mTags.setAdapter(autoCompleteInterestAdapter);
        }

    }

    //TODO: Create event task
    class CreateEventTask extends AsyncTask<Void, Void, String>
    {
        String message;
        boolean error = false;
        boolean allow = false;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(CreateEventActivity.this,
                    getString(R.string.create_event) + "...",
                    getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            ActsApi createEventApi = new ActsApi();
            createEventApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            createEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            Activity createEvent = new Activity();
            createEvent.setName(mEventName.getText().toString());
            createEvent.setDescription(mDescription.getText().toString());
            createEvent.setMax_attendees(Double.parseDouble(mMaxPeople.getText().toString()));

            createEvent.setCreator(PrefUtil.getString(getApplicationContext(),IAppConstants.USER_ID));
            createEvent.setTagged_interests(mTagsList);
            createEvent.setTimes(mDateTimeListItemsUTC);
            createEvent.setLocations(mEventLocationList);
            if(mAllowSuggestions.isChecked())
            {
                allow = true;
            }
            createEvent.setAllow_joiner_input(allow);

            try {
                createEventApi.createActivity(createEvent);
                message = "Event Created";
            } catch (ApiException e) {
                error = true;
                return e.getMessage();
            }
            PrefUtil.putBoolean(getBaseContext(), IAppConstants.BEFIT_CACHE_VALID_FLAG, false);
            PrefUtil.putBoolean(getBaseContext(), IAppConstants.NEAR_YOU_CACHE_VALID_FLAG, false);
            PrefUtil.putBoolean(getBaseContext(), IAppConstants.MY_EVENTS_CACHE_VALID_FLAG, false);
            PrefUtil.putBoolean(getBaseContext(), IAppConstants.INTEREST_EVENTS_CACHE_VALID_FLAG, false);
            return message;
        }

        @Override
        protected void onPostExecute(String address) {

            progressDialog.dismiss();

            if(error)
            {
                address = address.replace("\"", "");
                address = address.replace("{error:", "");
                address = address.replace("}", "");

                if(address.toLowerCase().contains("time"))
                {
                    mDateTimeError.setError(address);
                }

                Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
                // Invalidate the my events cache to get updated values
                PrefUtil.putBoolean(getApplicationContext(), IAppConstants.MY_EVENTS_CACHE_VALID_FLAG, false);
                finish();
            }
        }
    }



}
