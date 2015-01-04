package com.Fiplus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wordnik.client.api.UserApi;
import com.wordnik.client.model.Register;
import com.wordnik.client.model.Success;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Sign up activity
 */
public class SignUpActivity extends Activity {

    protected EditText signUpName;
    protected EditText signUpEmail;
    protected EditText signUpPassword;
    protected Button signUpButton;

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

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpTask signUpTask = new SignUpTask();
                signUpTask.execute();
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

   class SignUpTask extends AsyncTask<Void, Void, String>{

       @Override
       protected String doInBackground(Void... params) {

           UserApi userApi = new UserApi();
           userApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
           userApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

           Register register = new Register();
           register.setEmail(signUpEmail.getText().toString());
           register.setFirst_name(signUpName.getText().toString());
           register.setNew_password(signUpPassword.getText().toString());

           try{
               Success success = userApi.register(true, register);
               System.out.println(success.toString());
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
