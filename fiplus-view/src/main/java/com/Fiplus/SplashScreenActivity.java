package com.Fiplus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Credentials;

import utils.IAppConstants;
import utils.PrefUtil;

public class SplashScreenActivity extends BaseFragmentActivity {

    protected ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //TODO: Splashscreen handle landscape
        image = (ImageView) findViewById(R.id.imageSplash);

        // check for google play services and obtain the current regid
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
        }else{
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        new StartLogin().execute();
    }

    private class StartLogin extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean validSession = false;
            //String oldSessionKey = PrefUtil.getString(getApplicationContext(), IAppConstants.SESSION_ID, null);
            String userID = PrefUtil.getString(getApplicationContext(), IAppConstants.EMAIL, null);
            String userPass = PrefUtil.getString(getApplicationContext(), IAppConstants.PWD, null);
            String dspUrl = PrefUtil.getString(getApplicationContext(), IAppConstants.DSP_URL, null);

           // if(oldSessionKey == null &&  userID == null && userPass == null && dspUrl == null){
            if(userID == null && userPass == null && dspUrl == null){
                // show splash for 2 secs and go to login
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{ // previously logged in, check if still valid
                UsersApi userApi = new UsersApi();
                userApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
                userApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);
                try{
                    Credentials login = new Credentials();
                    login.setEmail(userID);
                    login.setPassword(userPass);
                    userApi.login(login);
                    validSession = true;
                    // check if the app was updated and send the new and current regid when the app is resumed
                    if (checkPlayServices()) {
                        if (regid.isEmpty()) {
                            registerInBackground();
                        }
                        else{
                            sendRegistrationIdToBackend(regid);
                        }
                    }
                    else {
                        Log.i(TAG, "No valid Google Play Services APK found.");
                    }
                }catch(Exception e){
                    try{
                        userApi.logout();
                    }catch(Exception e1){
                        return false;
                    }
                    PrefUtil.putString(getApplicationContext(), IAppConstants.EMAIL, "");
                    PrefUtil.putString(getApplicationContext(), IAppConstants.PWD, "");
                    PrefUtil.putString(getApplicationContext(), IAppConstants.USER_ID, "");
                }
            }
            return validSession;
        }
        @Override
        protected void onPostExecute(Boolean isValidSession) {
            if(isValidSession){
                Intent in= new Intent(SplashScreenActivity.this, MainScreenActivity.class);
                startActivity(in);
            }
            else {
                Intent in=new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(in);
            }
            finish();
        }
    }
}
