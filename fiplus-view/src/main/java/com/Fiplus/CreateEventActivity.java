package com.Fiplus;

import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wordnik.client.ApiException;
import com.wordnik.client.api.ActsApi;
import com.wordnik.client.model.Activity;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.Time;

import java.util.ArrayList;
import java.util.List;

import adapters.LocationArrayAdapterNoFilter;
import utils.DateTimePicker;
import utils.GeocodingLocation;
import utils.IAppConstants;
import utils.ListViewUtil;
import utils.PrefUtil;


public class CreateEventActivity extends FragmentActivity implements TextWatcher {

    private static final int MAX_CHARS = 3; //max chars before suggesting locations
    private static final int AUTOCOMPLETE = 5; //number of suggestions
    private static final int FINAL_LOC = 1; //when adding the location
    final String btnInnerHTML = "<font color='gray'>\"%s\"    </font>";
    final Integer MAX = 3;

    //protected ImageView mImageView;
    protected EditText mEventName;
    protected AutoCompleteTextView mEventLocation;
    protected Button mAddLocationBtn;
    protected ListView mLocationListView;
    protected EditText mDescription;
    protected Button mCreateButton;
    protected Button mCancelButton;
    protected EditText mMaxPeople;
    protected Button mDateTimeButton;
    protected ListView mDateTimeListView;
    protected EditText mTags;
    protected TextView mAddTags;
    protected EditText mDateTimeError;

    protected List<Location> mEventLocationList = new ArrayList<Location>();
    protected List<String> mEventLocationListItems = new ArrayList<String>();
    protected ArrayAdapter<String> locationAdapter;
    protected ArrayAdapter<String> autoCompleteLocationAdapter;

    protected List<Time> mDateTimeListItemsUTC = new ArrayList<Time>();
    protected List<String> mDateTimeListItems = new ArrayList<String>();
    protected ArrayAdapter<String> dateTimeAdapter;

    protected List<String> mTagsList = new ArrayList<String>();
    protected LinearLayout mTagsLinearLayout;

    //zero argument constructor
    public CreateEventActivity()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        setTitle(R.string.create_event_activity_title);

//        mImageView = (ImageView)findViewById(R.id.createEventImageView);
//        mImageView.setImageResource(R.drawable.fiplus);

        mEventName = (EditText) findViewById(R.id.create_event_name);
        mDescription = (EditText) findViewById(R.id.create_event_description);

        mEventLocation = (AutoCompleteTextView) findViewById(R.id.create_event_location);
        autoCompleteLocationAdapter = new LocationArrayAdapterNoFilter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteLocationAdapter.setNotifyOnChange(false);
        mEventLocation.addTextChangedListener(this);
        //mEventLocation.setOnItemSelectedListener(this);
        mEventLocation.setThreshold(MAX_CHARS);
        mEventLocation.setAdapter(autoCompleteLocationAdapter);


        mAddLocationBtn = (Button) findViewById(R.id.create_event_add_location);
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
                        GeocodingLocation location = new GeocodingLocation(getBaseContext(), CreateEventActivity.this, FINAL_LOC);
                        location.execute(address);
                    }

                }
            }
        });

        //to show the list of suggested locations
        mLocationListView = (ListView)findViewById(R.id.create_event_address_list);
        mLocationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mEventLocationList.remove(position);
                mEventLocationListItems.remove(position);
                locationAdapter.notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(mLocationListView);

                if(mEventLocationListItems.size() < MAX)
                {
                    mEventLocation.setHint(R.string.create_event_location_hint);
                    mEventLocation.setClickable(true);
                    mEventLocation.setEnabled(true);
                }
            }
        });

        //for locations
        locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mEventLocationListItems);
        mLocationListView.setAdapter(locationAdapter);
        ListViewUtil.setListViewHeightBasedOnChildren(mLocationListView);

        mMaxPeople = (EditText) findViewById(R.id.create_event_number_of_people);

        //for tags
        mTags = (EditText) findViewById(R.id.create_event_tags);
        mAddTags = (TextView) findViewById(R.id.create_event_tags_label);
        mTagsLinearLayout = (LinearLayout) findViewById(R.id.create_event_tags_list);

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
        mDateTimeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mDateTimeListItems.remove(position);
                mDateTimeListItemsUTC.remove(position);
                dateTimeAdapter.notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(mDateTimeListView);

                if(mDateTimeListItems.size() < MAX)
                {
                    mDateTimeButton.setText(getString(R.string.create_event_suggest_date_time));
                    mDateTimeButton.setEnabled(true);
                }
            }
        });

        //for date time
        dateTimeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDateTimeListItems);
        mDateTimeListView.setAdapter(dateTimeAdapter);
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
        dateTimeAdapter.notifyDataSetChanged();
        ListViewUtil.setListViewHeightBasedOnChildren(mDateTimeListView);

        if(mDateTimeListItems.size() == MAX)
        {
            //mDateTimeError.setText(getString(R.string.create_event_max_time));
            mDateTimeButton.setText(getString(R.string.create_event_max_time));
            mDateTimeButton.setEnabled(false);
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
            locationAdapter.notifyDataSetChanged();
            ListViewUtil.setListViewHeightBasedOnChildren(mLocationListView);

            if(mEventLocationListItems.size() == MAX)
            {
                mEventLocation.setHint(R.string.create_event_max_location);
                mEventLocation.setClickable(false);
                mEventLocation.setEnabled(false);
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
        int tagsAdded = mTagsList.size();
        mTags.setText("");

        if(!tag.isEmpty() && tagsAdded < 3)
        {
            mTagsList.add(tag);
            tagsAdded = mTagsList.size();

            final TextView createEventTag = new TextView(getBaseContext());
            createEventTag.setText(Html.fromHtml(String.format(btnInnerHTML, mTagsList.get(tagsAdded - 1))));
            createEventTag.setBackgroundResource(R.drawable.button_tags);
            createEventTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.tags_delete, 0);

            createEventTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeTag(view, createEventTag.getText().toString());
                }
            });

            int padding = getResources().getDimensionPixelOffset(R.dimen.tags_padding);
            createEventTag.setPadding(padding, 0, padding, 0);

            mTagsLinearLayout.addView(createEventTag);
            checkMaxTags();
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

        checkMaxTags();
    }

    private void checkMaxTags()
    {
        if(mTagsList.size() == MAX)
        {
            mTags.setHint(R.string.create_event_tags_hint);
            mTags.setClickable(false);
            mTags.setText("");
            mTags.setEnabled(false);
        }
        else
        {
            mTags.setHint("");
            mTags.setClickable(true);
            mTags.setEnabled(true);
        }
    }

    //TODO: Create event task
    class CreateEventTask extends AsyncTask<Void, Void, String>
    {
        String message;
        boolean error = false;

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
            createEvent.setSuggested_times(mDateTimeListItemsUTC);
            createEvent.setSuggested_locations(mEventLocationList);
            try {
                createEventApi.createActivity(createEvent);
                message = "Event Created";
            } catch (ApiException e) {
                error = true;
                return e.getMessage();
            }

            return message;
        }

        @Override
        protected void onPostExecute(String address) {

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
                //TODO: (Jobelle) delete an incomplete created events when error occurs
            }
            else
            {
                Toast.makeText(getBaseContext(), address, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }



}
