package com.Fiplus;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

/**
 * Sign up activity
 */
public class SignUpActivity extends Activity {

    protected EditText signUpName;
    protected EditText signUpEmail;
    protected EditText signUpPassword;
    protected Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //initialize
        signUpName = (EditText)findViewById(R.id.sign_up_name);
        signUpEmail = (EditText)findViewById(R.id.sign_up_email);
        signUpPassword = (EditText)findViewById(R.id.sign_up_password);
        signUpButton = (Button)findViewById(R.id.sign_up_button2);
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

}
