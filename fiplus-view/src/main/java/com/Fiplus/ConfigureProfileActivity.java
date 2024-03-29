package com.Fiplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wordnik.client.api.InterestsApi;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.UserProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapters.LocationArrayAdapterNoFilter;
import adapters.RemovableItemAdapter;
import utils.GeoAutoCompleteInterface;
import utils.IAppConstants;
import utils.ListViewUtil;
import utils.LocationUtil;
import utils.PrefUtil;


public class ConfigureProfileActivity extends Activity implements TextWatcher, GeoAutoCompleteInterface {

    protected ImageView mImageView;
    protected TextView mAddTextView;
    protected ListView mInterestListView;
    protected RemovableItemAdapter mRemovableItemAdapter;
    protected Button mSaveButton;
    protected Button mCancelButton;

    protected EditText mProfileName;
    protected EditText mGender;
    protected EditText mAge;

    protected AutoCompleteTextView mInterestInputField;
    protected ArrayAdapter<String> autoCompleteInterestAdapter;
    protected List<String> interestsList;

    protected AutoCompleteTextView mLocationInputField;
    protected ArrayAdapter<String> autoCompleteLocationAdapter;

    protected ArrayList<String> mInterestListItems = new ArrayList<String>();

    protected ArrayAdapter<String> listAdapter;

    protected Location userLocation;
    protected boolean signUpActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Tracker t = ((FiplusApplication)this.getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_profile);

        mLocationInputField = (AutoCompleteTextView) findViewById(R.id.configure_location_field);
        autoCompleteLocationAdapter = new LocationArrayAdapterNoFilter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteLocationAdapter.setNotifyOnChange(false);
        mLocationInputField.addTextChangedListener(this);
        mLocationInputField.setThreshold(MAX_CHARS);
        mLocationInputField.setAdapter(autoCompleteLocationAdapter);

        mSaveButton = (Button) findViewById(R.id.configure_save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkForValidInputs()) {
                    checkAddress();
                }
            }
        });

        mCancelButton = (Button) findViewById(R.id.configure_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //if first sign in, setup the UI to be different
        Bundle checkActivity = getIntent().getExtras();
        if(checkActivity!=null) //this means the calling activity is sign up
        {
            signUpActivity = true;
            getActionBar().setDisplayHomeAsUpEnabled(false);
            mCancelButton.setVisibility(View.GONE);
            //mSaveButton.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            p.weight = 1;
            mSaveButton.setLayoutParams(p);
        }

        //TODO: (Nick) Configure Profile - Upload Profile Photo
        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageResource(R.mipmap.ic_configure);

        mProfileName = (EditText) findViewById(R.id.configure_profile_name);
        mGender = (EditText) findViewById(R.id.configure_gender);

        mAge = (EditText) findViewById(R.id.configure_age);

        mInterestInputField = (AutoCompleteTextView) findViewById(R.id.interests_input_field);
        mInterestInputField.addTextChangedListener(new TextWatcher() {

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
        mInterestInputField.setThreshold(1);

        mInterestListView = (ListView) findViewById(R.id.interests_list);
        mRemovableItemAdapter = new RemovableItemAdapter(this, mInterestListItems, mInterestListView);
        mInterestListView.setAdapter(mRemovableItemAdapter);

        mInterestListView.setOnTouchListener(new ListView.OnTouchListener() {
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

        mAddTextView = (TextView) findViewById(R.id.add_interest_label);
        setTitle(R.string.configure_profile);

        //listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mInterestListItems);
        //mInterestListView.setAdapter(listAdapter);

        getProfile();
        ListViewUtil.setListViewHeightBasedOnChildren(mInterestListView);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

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
                utils.GeocodingLocation location = new utils.GeocodingLocation(getBaseContext(), ConfigureProfileActivity.this, AUTOCOMPLETE);
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

    public void populateLocation(Location location, String address) {
    }

    public void onAddInterestClick(View view) {
        mInterestListItems.add(mInterestInputField.getText().toString());
        mInterestInputField.setText("");
        mRemovableItemAdapter.notifyDataSetChanged();
        ListViewUtil.setListViewHeightBasedOnChildren(mInterestListView);
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

    private void getProfile() {
        GetProfileTask getProfileTask = new GetProfileTask();
        getProfileTask.execute();

        GetInterestsTask getInterestsTask = new GetInterestsTask();
        getInterestsTask.execute();
    }

    private boolean checkForValidInputs()
    {
        View focusView;

        if (TextUtils.isEmpty(mProfileName.getText())) {
            mProfileName.setError(getString(R.string.error_field_required));
            focusView = mProfileName;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void checkAddress(){
        String address = mLocationInputField.getText().toString();

        if(!address.isEmpty())
        {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) && Geocoder.isPresent())
            {
                GeocodingLocation location = new GeocodingLocation(getBaseContext());
                location.execute(address);
            }

        }
        else
        {
            userLocation = null;
            SaveProfileTask saveProfileTask = new SaveProfileTask();
            saveProfileTask.execute();
        }
    }

    class GetInterestsTask extends AsyncTask<Void, Void, String>
    {
        @Override
        protected String doInBackground(Void... params) {

            InterestsApi interests = new InterestsApi();
            interests.getInvoker().setContext(getBaseContext());
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
            mInterestInputField.setAdapter(autoCompleteInterestAdapter);
            mRemovableItemAdapter.notifyDataSetChanged();
        }

    }

    class GetProfileTask extends AsyncTask<Void, Void, String> {

        protected UserProfile response;

        @Override
        protected String doInBackground(Void... params) {

            UsersApi usersApi = new UsersApi();
            usersApi.getInvoker().setContext(getBaseContext());
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                response = usersApi.getUserProfile(PrefUtil.getString(getApplicationContext(), IAppConstants.USER_ID, null));
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result){
            if(response == null)
                return;
            Address addr;
            List<Address> addressList;
            if(response.getUsername() != null) mProfileName.setText(response.getUsername());
            if(response.getGender() != null) mGender.setText(response.getGender());
            if(response.getAge() != null)mAge.setText(String.valueOf(response.getAge().intValue()));
            if(response.getLocation().getLongitude() != null) {
                mLocationInputField.setText(LocationUtil.getLocationString(response.getLocation(), getBaseContext()));
            }

            if(response.getTagged_interests() != null) {
                for (int i = 0; i < response.getTagged_interests().size(); i++) {
                    mInterestListItems.add(response.getTagged_interests().get(i));
                }
            }
            ListViewUtil.setListViewHeightBasedOnChildren(mInterestListView);
            mRemovableItemAdapter.notifyDataSetChanged();
        }
    }

    class SaveProfileTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            UsersApi usersApi = new UsersApi();
            usersApi.getInvoker().setContext(getBaseContext());
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(mProfileName.getText().toString());
            userProfile.setEmail(PrefUtil.getString(getApplicationContext(), IAppConstants.EMAIL));
            if (!TextUtils.isEmpty(mAge.getText()))
                userProfile.setAge(Double.parseDouble(mAge.getText().toString()));
            userProfile.setGender(mGender.getText().toString().toUpperCase());
            userProfile.setTagged_interests(mInterestListItems);
            userProfile.setLocation(userLocation);

            try{
                usersApi.saveUserProfile(userProfile);
                PrefUtil.putBoolean(getBaseContext(), IAppConstants.NEAR_YOU_CACHE_VALID_FLAG, false);
                PrefUtil.putBoolean(getBaseContext(), IAppConstants.INTEREST_EVENTS_CACHE_VALID_FLAG, false);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){

            finish();
            if(signUpActivity)
            {
                Intent in= new Intent(ConfigureProfileActivity.this,MainScreenActivity.class);
                startActivity(in);
            }
            else
            {
                Toast.makeText(getBaseContext(), "Profile Saved", Toast.LENGTH_SHORT).show();
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
            progressDialog= ProgressDialog.show(ConfigureProfileActivity.this, getString(R.string.progress_dialog_title) + "...", getString(R.string.progress_dialog_text), true);
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
                String addressText = (addr.getMaxAddressLineIndex() > 0 ? addr.getAddressLine(0) : "")+" "+
                        (addr.getLocality() != null ? addr.getLocality() : "")
                        +" "+(addr.getCountryName() != null ? addr.getCountryName() : "");

                location.setLatitude(addr.getLatitude());
                location.setLongitude(addr.getLongitude());
                location.setAddress(addressText);
                userLocation = location;

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
                mLocationInputField.setText(address);
                SaveProfileTask saveProfileTask = new SaveProfileTask();
                saveProfileTask.execute();
            }
            else
            {
                mLocationInputField.setError(getString(R.string.error_address_not_found));
            }
            progressDialog.dismiss();
        }
    }
}