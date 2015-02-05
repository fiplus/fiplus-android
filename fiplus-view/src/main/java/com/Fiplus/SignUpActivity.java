package com.Fiplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Credentials;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utils.IAppConstants;

/**
 * Sign up activity
 */
public class SignUpActivity extends Activity {

    protected EditText signUpName;
    protected EditText signUpEmail;
    protected EditText signUpPassword;
    protected Button signUpButton;
    protected Button cancelButton;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        progressDialog = new ProgressDialog(SignUpActivity.this);

        //initialize
        signUpName = (EditText)findViewById(R.id.sign_up_name);
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

    public void attemptSignUp() {

        // Reset errors.
        signUpName.setError(null);
        signUpEmail.setError(null);
        signUpPassword.setError(null);

        //TODO: Update if Name will be added to Credentials
        // Store values at the time of the login attempt.
        String name = signUpName.getText().toString();
        String email = signUpEmail.getText().toString();
        String password = signUpPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address and password.
        if (TextUtils.isEmpty(name)) {
            signUpName.setError(getString(R.string.error_field_required));
            focusView = signUpName;
            cancel = true;
        }

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

       @Override
       protected String doInBackground(Void... params) {

           UsersApi userApi = new UsersApi();
           userApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
           userApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

           Credentials credentials = new Credentials();
           credentials.setEmail(signUpEmail.getText().toString());
           //credentials.setFirst_name(signUpName.getText().toString());
           credentials.setPassword(signUpPassword.getText().toString());

           try{
               userApi.registerUser(credentials);
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
               AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpActivity.this);
               alertDialog.setTitle("Message...").setMessage(errorMsg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dialog.cancel();
                   }
               });
               alertDialog.show();
           }
           else {
               Toast.makeText(getBaseContext(), "Sign up successful", Toast.LENGTH_SHORT).show();
               Intent in= new Intent(SignUpActivity.this,LoginActivity.class);
               startActivity(in);
               finish();
           }

       }

       @Override
       protected void onPreExecute() {
           progressDialog.show();
       }
   }
}
