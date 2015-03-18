package com.Fiplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.analytics.Tracker;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Credentials;
import com.wordnik.client.model.WhoAmI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.IAppConstants;
import utils.PrefUtil;

/**
 * Sign up activity
 */
public class SignUpActivity extends BaseFragmentActivity {

    protected EditText signUpEmail;
    protected EditText signUpPassword;
    protected Button signUpButton;
    protected Button cancelButton;
    private TextView mFitTerms;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Check device for Play Services APK. TODO: Check that GooglePlayServices is available on the device before calling methods
        // that require it. Must be done for all onResume() and onCreate() methods for each Activity (Allan). If not available
        // must disable features or prompt the user to download the latest GooglePlayServices
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);
        }else{
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        //initialize
        signUpEmail = (EditText)findViewById(R.id.sign_up_email);
        signUpPassword = (EditText)findViewById(R.id.sign_up_password);
        signUpButton = (Button)findViewById(R.id.sign_up_button2);
        cancelButton = (Button)findViewById(R.id.sign_up_cancel);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFitTerms = (TextView) findViewById(R.id.signUpPolicy);
        setPrivacyTerms();
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

    private void setPrivacyTerms()
    {
        String text = mFitTerms.getText().toString();
        int privacy1 = text.indexOf("Privacy");
        int privacy2 = privacy1 + 13;

        int terms1 = text.indexOf("Terms");
        int terms2 = terms1 + 11;

        mFitTerms.setMovementMethod(LinkMovementMethod.getInstance());
        mFitTerms.setText(text, TextView.BufferType.SPANNABLE);

        Spannable mySpannable = (Spannable)mFitTerms.getText();
        ClickableSpan privacySpan = new ClickableSpan()
        {
            @Override
            public void onClick(View widget) {
                Uri uriUrl = Uri.parse("http://fiplus.github.io/PrivacyPolicy.html");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        };

        ClickableSpan termsSpan = new ClickableSpan()
        {
            @Override
            public void onClick(View widget) {
                Uri uriUrl = Uri.parse("http://fiplus.github.io/TermsOfUse.html");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        };
        mySpannable.setSpan(privacySpan, privacy1, privacy2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mySpannable.setSpan(termsSpan, terms1, terms2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public void attemptSignUp() {

        // Reset errors.
        signUpEmail.setError(null);
        signUpPassword.setError(null);

        //TODO: Update if Name will be added to Credentials
        // Store values at the time of the login attempt.
        String email = signUpEmail.getText().toString();
        String password = signUpPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)) {
            signUpEmail.setError(getString(R.string.error_field_required));
            focusView = signUpEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            signUpEmail.setError(getString(R.string.error_invalid_email));
            focusView = signUpEmail;
            cancel = true;
        }

        if(TextUtils.isEmpty(password))
        {
            signUpPassword.setError(getString(R.string.error_field_required));
            focusView = signUpPassword;
            cancel = true;
        }
        else if(password.length() < 5)
        {
            signUpPassword.setError(getString(R.string.error_password_length));
            focusView = signUpPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            SignUpTask signUpTask = new SignUpTask();
            signUpTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

   class SignUpTask extends AsyncTask<Void, Void, String>{

       String email;
       String password;

       public SignUpTask()
       {
           email = signUpEmail.getText().toString();
           password = signUpPassword.getText().toString();
       }

       @Override
       protected String doInBackground(Void... params) {

           UsersApi userApi = new UsersApi();
           userApi.getInvoker().setContext(getBaseContext());
           userApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

           Credentials credentials = new Credentials();
           credentials.setEmail(email);
           credentials.setPassword(password);

           try{
               userApi.registerUser(credentials);

               userApi.getInvoker().setContext(getApplicationContext());
               userApi.login(credentials);

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

               WhoAmI id = userApi.whoAmI();

               PrefUtil.putString(getApplicationContext(), IAppConstants.DSP_URL, IAppConstants.DSP_URL);
               PrefUtil.putString(getApplicationContext(), IAppConstants.EMAIL, email);
               PrefUtil.putString(getApplicationContext(), IAppConstants.PWD, password);
               PrefUtil.putString(getApplicationContext(), IAppConstants.USER_ID, id.getUser_id());
           } catch (Exception e) {
               return e.getMessage();
           }
           return null;
       }

       @Override
       protected void onPostExecute(String result) {
           progressDialog.cancel();
           if (result !=null){
               String errorMsg = "";
               try {
                   JSONObject jObj = new JSONObject(result);
                   JSONArray jArray = jObj.getJSONArray("error");
                   JSONObject obj = jArray.getJSONObject(0);
                   errorMsg = obj.getString("message");
               } catch (JSONException e) {
                   errorMsg = result;
               }

               errorMsg = errorMsg.replace("\"", "");
               errorMsg = errorMsg.replace("{error:", "");
               errorMsg = errorMsg.replace("}", "");

               AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
               alertDialog.setTitle("Error...").setMessage(errorMsg).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
               alertDialog.show();
           }
           else {
               Toast.makeText(getBaseContext(), "Sign up successful", Toast.LENGTH_SHORT).show();
               Intent in= new Intent(SignUpActivity.this,ConfigureProfileActivity.class);
               in.putExtra("class", ConfigureProfileActivity.class.toString());

               Tracker t = ((FiplusApplication)getApplication()).getTracker();
               t.send(new HitBuilders.EventBuilder().setCategory(FiplusApplication.ACCOUNTS_CATEGORY)
                       .setAction(FiplusApplication.SIGNUP_ACTION).build());

               startActivity(in);
               finish();
           }

       }

       @Override
       protected void onPreExecute() {
           progressDialog = ProgressDialog.show(SignUpActivity.this, "Signing up...", getString(R.string.progress_dialog_text), true);
       }
   }
}
