package com.Fiplus;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wordnik.client.api.ActivityApi;
import com.wordnik.client.model.Activity;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.Time;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import utils.IAppConstants;
import utils.ListViewUtil;


public class CreateEventActivity extends FragmentActivity {

    static final String DATEFORMAT = "MM-dd-yyyy HH:mm a";
    final String btnInnerHTML = "<font color='gray'>\"%s\"    </font>";
    final Integer TAGS = 3;

    protected ImageView mImageView;
    protected EditText mEventName;
    protected EditText mEventLocation;
    protected EditText mDescription;
    protected Button mCreateButton;
    protected Button mCancelButton;
    protected Button mDateTimeButton;
    protected EditText mMaxPeople;
    protected ListView mDateTimeListView;
    protected EditText mTags;
    protected TextView mAddTags;

    protected List<Location> mEventLocationList = new ArrayList<Location>();

    protected List<Time> mDateTimeListItemsUTC = new ArrayList<Time>();
    protected List<String> mDateTimeListItems = new ArrayList<String>();
    protected ArrayAdapter<String> dateTimeAdapter;

    protected List<String> mTagsList = new ArrayList<String>();
    protected ArrayList<TextView> mTagArray = new ArrayList<TextView>();

    protected Button mSetTimeButton;
    protected Button mCancelTimeButton;
    protected DatePicker mDatePicker;
    protected TimePicker mTimePicker;

    private Dialog mDateTimeDialog;
    private String mStart = "start", mEnd = "end";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        setTitle(R.string.create_event_activity_title);

        mImageView = (ImageView)findViewById(R.id.createEventImageView);
        mImageView.setImageResource(R.drawable.fiplus);

        mEventName = (EditText) findViewById(R.id.create_event_name);
        mDescription = (EditText) findViewById(R.id.create_event_description);
        mEventLocation = (EditText) findViewById(R.id.create_event_location);
        mMaxPeople = (EditText) findViewById(R.id.create_event_number_of_people);

        //for tags
        mTags = (EditText) findViewById(R.id.create_event_tags);
        mAddTags = (TextView) findViewById(R.id.create_event_tags_label);
        mTagArray.add((TextView) findViewById(R.id.create_event_tag1));
        mTagArray.get(0).setVisibility(View.GONE);
        mTagArray.add((TextView) findViewById(R.id.create_event_tag2));
        mTagArray.get(1).setVisibility(View.GONE);
        mTagArray.add((TextView) findViewById(R.id.create_event_tag3));
        mTagArray.get(2).setVisibility(View.GONE);

        mDateTimeButton = (Button) findViewById(R.id.create_event_suggest_date_time);
        mDateTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Time time = new Time();
                showDateTimePickerDialog(time);
            }
        });

        //to show the list of suggest start and end date/time
        mDateTimeListView = (ListView)findViewById(R.id.create_event_datetimelist);
        mDateTimeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mDateTimeListItems.remove(position);
                mDateTimeListItemsUTC.remove(position);
                dateTimeAdapter.notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(mDateTimeListView);
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
        boolean error = false;
        View focusView = null;

        // Reset errors.
        mEventName.setError(null);
        mEventLocation.setError(null);
        mDescription.setError(null);
        mMaxPeople.setError(null);

        // Store values at the time of the create event attempt.
        String event_name = mEventName.getText().toString();
        String address = mEventLocation.getText().toString();
        String description = mDescription.getText().toString();
        String maxPeople = mMaxPeople.getText().toString();

        // Check for mandatory field
        if (TextUtils.isEmpty(event_name)) {
            mEventName.setError(getString(R.string.error_field_required));
            focusView = mEventName;
            error = true;
        }
        else if (TextUtils.isEmpty(address))
        {
            mEventLocation.setError(getString(R.string.error_field_required));
            focusView = mEventLocation;
            error = true;
        }
        else if (TextUtils.isEmpty(description))
        {
            mDescription.setError(getString(R.string.error_field_required));
            focusView = mDescription;
            error = true;
        }
        else if (TextUtils.isEmpty(maxPeople)) {
            mMaxPeople.setError(getString(R.string.error_field_required));
            focusView = mMaxPeople;
            error = true;
        }
        else if (mDateTimeListItems.isEmpty())
        {

        }

        if (error) {
            // There was an error; don't attempt to create event and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) && Geocoder.isPresent())
            {
                GeocodingLocation location = new GeocodingLocation(this);
                location.execute(address);
            }
        }
    }

    public void showDateTimePickerDialog(final Time time) {

        mDateTimeDialog = new Dialog(this);
        mDateTimeDialog.setContentView(R.layout.date_time_layout);
        mDateTimeDialog.setTitle(R.string.create_event_start_time);
        mDateTimeDialog.show();

        mDatePicker = (DatePicker) mDateTimeDialog.findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) mDateTimeDialog.findViewById(R.id.timePicker);

        //for cancel button
        mCancelTimeButton = (Button) mDateTimeDialog.findViewById(R.id.cancelPicker);
        mCancelTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDateTimeDialog.dismiss();
            }
        });

        //for set button
        mSetTimeButton = (Button) mDateTimeDialog.findViewById(R.id.setPicker);
        mSetTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int month = mDatePicker.getMonth();
                int year = mDatePicker.getYear();
                int day = mDatePicker.getDayOfMonth();
                int hour = mTimePicker.getCurrentHour();
                int minutes = mTimePicker.getCurrentMinute();

                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day, hour, minutes);

                time.setStart(cal.getTime().getTime());

                mDateTimeListItems.add(convertToTimeToString(time));
                mDateTimeListItemsUTC.add(time);
                dateTimeAdapter.notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(mDateTimeListView);

                mDateTimeDialog.dismiss();
            }
        });
    }

    private String convertToTimeToString(Time time)
    {
        long dateColumn = time.getStart();
        Date d = new Date(dateColumn);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        return dateFormat.format(d);  // formatted date in string
    }

    //Add event tags
    public void onAddTagsClick(View view)
    {
        String tag;
        tag = mTags.getText().toString();
        mTags.setText("");

        if(!tag.isEmpty() && mTagsList.size() < 3)
        {
            mTagsList.add(tag);
            int i = mTagsList.size();
            switch(i)
            {
                case 1: mTagArray.get(0).setText(Html.fromHtml(String.format(btnInnerHTML, mTagsList.get(i - 1))));
                    mTagArray.get(0).setVisibility(View.VISIBLE);
                    break;
                case 2: mTagArray.get(1).setText(Html.fromHtml(String.format(btnInnerHTML, mTagsList.get(i - 1))));
                    mTagArray.get(1).setVisibility(View.VISIBLE);
                    break;
                case 3:
                    checkMaxTags();
                    mTagArray.get(2).setText(Html.fromHtml(String.format(btnInnerHTML, mTagsList.get(i - 1))));
                    mTagArray.get(2).setVisibility(View.VISIBLE);
                    break;
            }
        }

        Log.d("Updated Add:", mTagsList.toString());
    }


    //to remove tag and update the list
    public void removeTag(View v)
    {
        v.setVisibility(View.GONE);
        switch (v.getId())
        {
            case R.id.create_event_tag1:
                mTagsList.remove(0);
                break;
            case R.id.create_event_tag2:
                mTagsList.remove(1);
                break;
            case R.id.create_event_tag3:
                mTagsList.remove(2);
                break;
        }

        //to move the array
        for(int i=0; i < mTagsList.size(); i++)
        {
            mTagArray.get(i).setText(Html.fromHtml(String.format(btnInnerHTML, mTagsList.get(i))));
            mTagArray.get(i).setVisibility(View.VISIBLE);
        }

        //to remove unnecessary tags
        for(int i=mTagsList.size(); i < TAGS; i++)
        {
            mTagArray.get(i).setVisibility(View.GONE);
        }

        checkMaxTags();
        Log.d("Updated Remove:", mTagsList.toString());
    }

    private void checkMaxTags()
    {
        if(mTagsList.size() == TAGS)
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
        @Override
        protected String doInBackground(Void... params)
        {
            ActivityApi createEventApi = new ActivityApi();
            createEventApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            createEventApi.setBasePath("http://dev-fiplus.bitnamiapp.com:8529/_db/fiplus/extensions");

            Activity createEvent = new Activity();
            createEvent.setName(mEventName.getText().toString());
            createEvent.setDescription(mDescription.getText().toString());
            createEvent.setMax_attendees(Integer.parseInt(mMaxPeople.getText().toString()));
            //createEvent.setCreator(IAppConstants.SESSION_ID);
            createEvent.setTagged_interests(mTagsList);
            createEvent.setSuggested_times(mDateTimeListItemsUTC);
            createEvent.setSuggested_locations(mEventLocationList);
            //try {
                //createEventApi.createActivity(createEvent);

//            } catch (ApiException e) {
//                return e.getMessage();
//            }

            return null;
        }

        @Override
        protected void onPostExecute(String address) {
            Toast.makeText(getBaseContext(), "Event Created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class GeocodingLocation extends AsyncTask<String, Void, String>
    {
        Context mContext;
        List<Address> addressList = null;
        Address addr;
        Location location = new Location();

        public GeocodingLocation(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected String doInBackground(String... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            // Get the current location from the input parameter list
            String loc = params[0];

            int i = 0;
            try {
                //try 3 times in case geocoder doesn't work the first time
                do {
                    addressList = geocoder.getFromLocationName(loc, 1);
                    i++;
                } while (addressList.size()==0 && i < 3);

            } catch (IOException e1) {
                Log.e("LocationSampleActivity",
                        "IO Exception in getFromLocation()");
                e1.printStackTrace();
            }

            // If the geocode returned an address
            if (addressList != null && addressList.size() > 0) {

                addr = addressList.get(0);
                location.setLatitude(addr.getLatitude());
                location.setLongitude(addr.getLongitude());

                /*
                 * Format the first line of address (if available),
                 * city (if available), and country name.
                 */
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        addr.getMaxAddressLineIndex() > 0 ?
                                addr.getAddressLine(0) : "",
                        // Locality is usually a city
                        addr.getLocality() != null ? addr.getLocality() : "",
                        // The country of the address
                        addr.getCountryName());
                // Return the text
                return addressText;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String address) {
            // Display the results of the lookup.
            if(address != null)
            {
                mEventLocationList.add(location);
                mEventLocation.setText(address);
                CreateEventTask createEventTask = new CreateEventTask();
                createEventTask.execute();
            }
            else
            {
                mEventLocation.setError(getString(R.string.error_address_not_found));
                View focusView = mEventLocation;
                focusView.requestFocus();
            }
        }
    }

}
