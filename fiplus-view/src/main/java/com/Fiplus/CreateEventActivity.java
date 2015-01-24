package com.Fiplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.wordnik.client.ApiException;
import com.wordnik.client.api.ActsApi;
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
    final Integer MAX = 3;

    protected ImageView mImageView;
    protected EditText mEventName;
    protected EditText mEventLocation;
    protected Button mAddLocationBtn;
    protected ListView mLocationListView;
    protected EditText mDescription;
    protected Button mCreateButton;
    protected Button mCancelButton;
    protected Button mDateTimeButton;
    protected EditText mMaxPeople;
    protected ListView mDateTimeListView;
    protected EditText mTags;
    protected TextView mAddTags;
    protected EditText mDateTimeError;

    protected List<Location> mEventLocationList = new ArrayList<Location>();
    protected List<String> mEventLocationListItems = new ArrayList<String>();
    protected ArrayAdapter<String> locationAdapter;

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
                        GeocodingLocation location = new GeocodingLocation(getBaseContext());
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
        mTagArray.add((TextView) findViewById(R.id.create_event_tag1));
        mTagArray.get(0).setVisibility(View.GONE);
        mTagArray.add((TextView) findViewById(R.id.create_event_tag2));
        mTagArray.get(1).setVisibility(View.GONE);
        mTagArray.add((TextView) findViewById(R.id.create_event_tag3));
        mTagArray.get(2).setVisibility(View.GONE);

        mDateTimeError = (EditText) findViewById(R.id.create_event_datetime_error);
        mDateTimeButton = (Button) findViewById(R.id.create_event_suggest_date_time);
        mDateTimeButton.setText(getString(R.string.create_event_suggest_date_time) + " [" + MAX + "]");
        mDateTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDateTimeError.setError(null);
                Time time = new Time();
                showDateTimePickerDialog(time, mStart);
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

                int maxDateTime = MAX - mDateTimeListItems.size();

                if(mDateTimeListItems.size() < MAX)
                {
                    mDateTimeButton.setText(getString(R.string.create_event_suggest_date_time) + " [" + maxDateTime + "]");
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
        mDescription.setError(null);
        mMaxPeople.setError(null);
        mDateTimeError.setError(null);

        // Store values at the time of the create event attempt.
        String event_name = mEventName.getText().toString();
        String description = mDescription.getText().toString();
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

        if (TextUtils.isEmpty(description))
        {
            mDescription.setError(getString(R.string.error_field_required));
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

    public void showDateTimePickerDialog(final Time time, final String startEnd) {

        mDateTimeDialog = new Dialog(this);
        mDateTimeDialog.setContentView(R.layout.date_time_layout);

        if(startEnd.equalsIgnoreCase("start"))
        {
            mDateTimeDialog.setTitle(R.string.create_event_start_time);
        }
        else
        {
            mDateTimeDialog.setTitle(R.string.create_event_end_time);
        }

        mDateTimeDialog.show();

        mDatePicker = (DatePicker) mDateTimeDialog.findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) mDateTimeDialog.findViewById(R.id.timePicker);

        //for cancel button
        mCancelTimeButton = (Button) mDateTimeDialog.findViewById(R.id.cancelPicker);
        mCancelTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startEnd.equalsIgnoreCase("end"))
                {
                    //TODO: (Jobelle) Implement end time as optional
                    //this should still create the start time
                }
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

                Long lTime = cal.getTime().getTime();

                if(startEnd.equalsIgnoreCase("start"))
                {

                    time.setStart(lTime.doubleValue());
                    mDateTimeDialog.dismiss();
                    if(checkTime(time))
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateEventActivity.this);
                        alertDialog.setTitle("Error").setMessage(R.string.start_time_error).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                    else
                    {
                        //set end date if start time is set properly
                        showDateTimePickerDialog(time, mEnd);
                    }

                }
                else
                {
                    time.setEnd(lTime.doubleValue());
                    mDateTimeDialog.dismiss();
                    if(checkTime(time))
                    {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateEventActivity.this);
                        alertDialog.setTitle("Error").setMessage(R.string.end_time_error).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                    }
                    else
                    {
                        mDateTimeListItems.add(convertToTimeToString(time));
                        mDateTimeListItemsUTC.add(time);
                        dateTimeAdapter.notifyDataSetChanged();
                        ListViewUtil.setListViewHeightBasedOnChildren(mDateTimeListView);

                        int maxDateTime = MAX - mDateTimeListItems.size();
                        mDateTimeButton.setText(getString(R.string.create_event_suggest_date_time) + " [" + maxDateTime + "]");

                        if(mDateTimeListItems.size() == MAX)
                        {
                            mDateTimeButton.setEnabled(false);
                        }
                    }
                }
            }
        });
    }

    private boolean checkTime(Time time)
    {
        boolean isError;

        //get current date and date
        Calendar c = Calendar.getInstance();

        if(time.getEnd() == null)
        {
            isError = time.getStart() < c.getTime().getTime();
        }
        else
        {
            isError = time.getEnd() < time.getStart();
        }

        return isError;
    }

    private String convertToTimeToString(Time time)
    {
        long startDate = Double.doubleToLongBits(time.getStart());
        long endDate = Double.doubleToLongBits(time.getEnd());
        Date d1 = new Date(startDate);
        Date d2 = new Date(endDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        return "Start: " + dateFormat.format(d1) + "\nEnd  : " + dateFormat.format(d2);
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
        for(int i=mTagsList.size(); i < MAX; i++)
        {
            mTagArray.get(i).setVisibility(View.GONE);
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

            //TODO: (Jobelle) Remove hardcoded setCreator call
            createEvent.setCreator("744777067851");
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

    private class GeocodingLocation extends AsyncTask<String, Void, String>
    {
        ProgressDialog progressDialog;
        Context mContext;
        List<Address> addressList = null;
        Address addr;
        Location location = new Location();

        public GeocodingLocation(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog= ProgressDialog.show(CreateEventActivity.this, getString(R.string.progress_dialog_title) + "...", getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(String... params) {
            Geocoder geocoder = new Geocoder(mContext, Locale.CANADA);

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
                // Return the text
                return addressText;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String address) {

            super.onPostExecute(address);

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

            progressDialog.dismiss();
        }
    }

}
