package com.Fiplus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;

import com.wordnik.client.ApiException;
import com.wordnik.client.api.UsersApi;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wordnik.client.model.SetDeviceId;

import java.io.IOException;

import utils.IAppConstants;
import utils.PrefUtil;

/**
 * Created by jsfirme on 15-01-04.
 */
public class BaseFragmentActivity extends FragmentActivity {
    protected String dsp_url;
    //protected String session_id;
    // GCM items
    GoogleCloudMessaging gcm;
    String replacedregid;
    String regid;
    Context context;
    // Google API Project ID
    String SENDER_ID = "566736661694";
    protected static final String TAG = BaseFragmentActivity.class.getSimpleName();
    protected static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        //TODO: handle versions with SDK version less than 11
//        if (android.os.Build.VERSION.SDK_INT >= 11){
//            try{
//                ActionBar actionbar = getActionBar();
//                actionbar.setTitle("");
//                actionbar.setIcon(R.drawable.df_logo_txt);
//            }catch(Exception e){
//
//            }
//        }
        dsp_url = PrefUtil.getString(getApplicationContext(), IAppConstants.DSP_URL);
        dsp_url += IAppConstants.DSP_URL_SUFIX;
        //session_id = PrefUtil.getString(getApplicationContext(), IAppConstants.SESSION_ID);
    }

    protected void log(String message){
        System.out.println("log: " + message);
    }

    protected void logout(){

        //TODO: Logout
        UsersApi usersApi = new UsersApi();
        usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
        usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);
        try{
            usersApi.logout();
        } catch (ApiException e){
            Log.e("BaseFragmentActivity", e.getMessage());
        }
        PrefUtil.putString(getApplicationContext(), IAppConstants.EMAIL, "");
        PrefUtil.putString(getApplicationContext(), IAppConstants.PWD, "");
        PrefUtil.putString(getApplicationContext(), IAppConstants.USER_ID, "");

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     * @return registration ID, or empty string if there is no existing registration ID.
     * TODO: Return old regid to backend server if a new one is require so the backend can differentiate between a new device vs updated app/uninstallation
     */
    protected String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        replacedregid = null;
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // App persists the registration ID in shared preferences
        return getSharedPreferences(MainScreenActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * Stores the registration ID and app versionCode in the application's shared preferences.
     */
    protected void registerInBackground() {
        RegisterInBackgroundTask gcmRegistration = new RegisterInBackgroundTask();
        gcmRegistration.execute();
    }

    class RegisterInBackgroundTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + regid;
                sendRegistrationIdToBackend(regid);
                // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform exponential back-off.
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     * TODO: Send regid to server (Allan)
     * You should send the registration ID to your server over HTTP,
     * so it can use GCM/HTTP or CCS to send messages to your app.
     * The request to your server should be authenticated if your app
     * is using accounts.
     */
    private void sendRegistrationIdToBackend(String regid) {
        Log.i("My tag", "The regID is:" + regid);

        UsersApi userApi = new UsersApi();
        userApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
        userApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

        SetDeviceId DeviceID = new SetDeviceId();
        // TODO: Make setCurrent_device_id return the current id, and setNew return the latest ID from GCM in case the ID changes
        DeviceID.setNew_device_id(regid);

        try{
            userApi.setDeviceId(DeviceID);
        } catch (Exception e) {
            Log.i(TAG, "", e);
        }
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    protected boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    // TODO: Handle Registration Errors (Allan)
}
