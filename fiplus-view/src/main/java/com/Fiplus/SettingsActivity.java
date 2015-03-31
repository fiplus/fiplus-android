package com.Fiplus;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;


public class SettingsActivity extends Activity {

    Switch mPushNotif;
    TextView mHelp, mTermsOfUse, mPrivacyPolicy, mFeedback, mRateApp;

    @Override
    protected void onCreate(Bundle savedInstancesState)
    {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_settings);

        mPushNotif = (Switch) findViewById(R.id.setting_push_notif_switch);

        mHelp = (TextView) findViewById(R.id.setting_help);
        mHelp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Uri uriUrl = Uri.parse("http://fiplus.github.io/#!page-help");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        mTermsOfUse = (TextView) findViewById(R.id.setting_terms_of_use);
        mTermsOfUse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Uri uriUrl = Uri.parse("http://fiplus.github.io/TermsOfUse.html");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        mPrivacyPolicy = (TextView) findViewById(R.id.setting_privacy);
        mPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Uri uriUrl = Uri.parse("http://fiplus.github.io/PrivacyPolicy.html");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        mFeedback = (TextView) findViewById(R.id.setting_feedback);
        mFeedback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Uri uriUrl = Uri.
                        parse("https://docs.google.com/forms/d/1IJanmZWGyjQ2f8rQuzSDyo8Y8PKvV8Ed4wrzGRkXoNI/viewform?usp=send_form");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        mRateApp = (TextView) findViewById(R.id.setting_rate);
        mRateApp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Uri uriUrl = Uri.parse("https://play.google.com/store/apps/details?id=com.Fiplus");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
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
}
