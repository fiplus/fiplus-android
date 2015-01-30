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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wordnik.client.api.UsersApi;

import com.wordnik.client.model.Location;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.UserProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import utils.IAppConstants;
import utils.ListViewUtil;
import utils.PrefUtil;


public class ConfigureProfileActivity extends Activity {

    protected ImageView mImageView;
    protected TextView mAddTextView;
    protected ListView mInterestListView;
    protected Button mSaveButton;

    protected EditText mProfileName;
    protected EditText mGender;
    protected EditText mAge;
    protected EditText mInterestInputField;
    protected EditText mLocationInputField;

    protected ArrayList<String> mInterestListItems = new ArrayList<String>();

    protected ArrayAdapter<String> listAdapter;

    protected Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_profile);

        mLocationInputField = (EditText) findViewById(R.id.configure_location_field);

        mSaveButton = (Button) findViewById(R.id.configure_save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveProfileTask saveProfileTask = new SaveProfileTask();
                saveProfileTask.execute();
            }
        });

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageResource(R.drawable.fiplus);

        mProfileName = (EditText) findViewById(R.id.configure_profile_name);
        mGender = (EditText) findViewById(R.id.configure_gender);
        mAge = (EditText) findViewById(R.id.configure_age);

        mInterestInputField = (EditText) findViewById(R.id.interests_input_field);

        mInterestListView = (ListView) findViewById(R.id.interests_list);
        mInterestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mInterestListItems.remove(position);
                listAdapter.notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(mInterestListView);
            }
        });

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

        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mInterestListItems);
        mInterestListView.setAdapter(listAdapter);


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

    public void onAddInterestClick(View view) {
        mInterestListItems.add(mInterestInputField.getText().toString());
        mInterestInputField.setText("");
        listAdapter.notifyDataSetChanged();
        ListViewUtil.setListViewHeightBasedOnChildren(mInterestListView);
    }

    public void onAddLocationClick(View v){
        String address = mLocationInputField.getText().toString();

        if(!address.isEmpty())
        {
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) && Geocoder.isPresent())
            {
                GeocodingLocation location = new GeocodingLocation(getBaseContext());
                location.execute(address);
            }

        }
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
    }

    class GetProfileTask extends AsyncTask<Void, Void, String> {

        protected UserProfile response;

        @Override
        protected String doInBackground(Void... params) {

            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath("http://dev-fiplus.bitnamiapp.com:8529/_db/fiplus/extensions");

            try {
                response = usersApi.getUserProfile(PrefUtil.getString(getApplicationContext(), IAppConstants.EMAIL, null));
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
            if(response.getAge() != null)mAge.setText(response.getAge().toString());

            if(response.getLocation().getLongitude() != null) {
                try {
                    Geocoder geocoder = new Geocoder(getBaseContext(), Locale.CANADA);
                    addressList = geocoder.getFromLocation(response.getLocation().getLatitude(),
                            response.getLocation().getLongitude(),
                            1);
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
                        // Return the text
                        mLocationInputField.setText(addressText);
                    }
                } catch (IOException e) {
                    Log.e("Configure Profile", e.getMessage());
                }
            }

            if(response.getTagged_interests() != null) {
                for (int i = 0; i < response.getTagged_interests().size(); i++) {
                    mInterestListItems.add(response.getTagged_interests().get(i));
                }
            }
            ListViewUtil.setListViewHeightBasedOnChildren(mInterestListView);
            listAdapter.notifyDataSetChanged();
        }
    }

    class SaveProfileTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(mProfileName.getText().toString());
            userProfile.setEmail(PrefUtil.getString(getApplicationContext(), IAppConstants.EMAIL));
            userProfile.setAge(Double.parseDouble(mAge.getText().toString()));
            userProfile.setGender(mGender.getText().toString());
            userProfile.setTagged_interests(mInterestListItems);
            userProfile.setLocation(userLocation);

            try{
                usersApi.saveUserProfile(userProfile);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            Intent in= new Intent(ConfigureProfileActivity.this, MainScreenActivity.class);
            startActivity(in);
            Toast.makeText(getBaseContext(), "Profile Saved", Toast.LENGTH_SHORT).show();
            finish();
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
                location.setLatitude(addr.getLatitude());
                location.setLongitude(addr.getLongitude());
                userLocation = location;

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
                mLocationInputField.setText(address);
            }
            else
            {
                mLocationInputField.setError(getString(R.string.error_address_not_found));
            }
            progressDialog.dismiss();
        }
    }
}