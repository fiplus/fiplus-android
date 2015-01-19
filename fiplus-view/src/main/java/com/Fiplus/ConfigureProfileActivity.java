package com.Fiplus;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.wordnik.client.api.UserfiApi;
import com.wordnik.client.model.UserProfile;
import com.wordnik.client.model.UserProfileDetailResponse;

import org.json.JSONException;

import java.util.ArrayList;

import utils.IAppConstants;
import utils.ListViewUtil;
import utils.PrefUtil;


public class ConfigureProfileActivity extends Activity {

    protected ImageView mImageView;
    protected TextView mAddTextView;
    protected Spinner mInterestSpinner;
    protected ListView mInterestListView;
    protected Button mSaveButton;

    protected EditText mProfileName;
    protected EditText mGender;
    protected EditText mAge;

    protected ArrayList<String> mInterestListItems = new ArrayList<String>();

    protected ArrayAdapter<String> listAdapter;
    protected ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_profile);

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

        mInterestSpinner = (Spinner) findViewById(R.id.interests_spinner);

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

        Spinner spinner = (Spinner) findViewById(R.id.interests_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.interest_spinner_items, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

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

    public void onAddClick(View view) {
        mInterestListItems.add(mInterestSpinner.getSelectedItem().toString());
        listAdapter.notifyDataSetChanged();
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
    }

    class GetProfileTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            UserfiApi userfiApi = new UserfiApi();
            userfiApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            userfiApi.setBasePath("http://dev-fiplus.bitnamiapp.com:8529/_db/fiplus/extensions");

            try {
                UserProfileDetailResponse response = userfiApi.GetUserProfile(PrefUtil.getString(getApplicationContext(), IAppConstants.EMAIL, null));

                mProfileName.setText(response.getUsername().toString());
                mGender.setText(response.getGender().toString());
                mAge.setText(response.getAge().toString());

                for(int i = 0; i < response.getTagged_interests().size(); i++) {
                    mInterestListItems.add(response.getTagged_interests().get(i).toString());
                }
                listAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

    }

    class SaveProfileTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            UserfiApi userfiApi = new UserfiApi();
            userfiApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            userfiApi.setBasePath("http://dev-fiplus.bitnamiapp.com:8529/_db/fiplus/extensions");

            UserProfile userProfile = new UserProfile();
            userProfile.setUsername(mProfileName.getText().toString());
            userProfile.setEmail(PrefUtil.getString(getApplicationContext(), IAppConstants.EMAIL, null));
            userProfile.setAge(Integer.parseInt(mAge.getText().toString()));
            userProfile.setGender(mGender.getText().toString());
            userProfile.setTagged_interests(mInterestListItems);

            try{
                String response = userfiApi.saveUserProfile(userProfile);
                System.out.println(response.toString());
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            Intent in= new Intent(ConfigureProfileActivity.this, MainScreenActivity.class);
            startActivity(in);
            finish();
        }
    }
}