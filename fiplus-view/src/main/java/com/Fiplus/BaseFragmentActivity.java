package com.Fiplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import utils.IAppConstants;
import utils.PrefUtil;

/**
 * Created by jsfirme on 15-01-04.
 */
public class BaseFragmentActivity extends FragmentActivity {
    protected String dsp_url;
    protected String session_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        session_id = PrefUtil.getString(getApplicationContext(), IAppConstants.SESSION_ID);
    }

    protected void log(String message){
        System.out.println("log: " + message);
    }

    protected void logout(){
        PrefUtil.putString(getApplicationContext(), IAppConstants.SESSION_ID, "");
        PrefUtil.putString(getApplicationContext(), IAppConstants.EMAIL, "");
        PrefUtil.putString(getApplicationContext(), IAppConstants.PWD, "");
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }
}
