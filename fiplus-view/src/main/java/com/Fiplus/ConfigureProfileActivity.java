package com.Fiplus;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class ConfigureProfileActivity extends Activity {

    protected ImageView mImageView;
    protected TextView mAddTextView;
    protected Spinner mInterestSpinner;
    protected ListView mInterestListView;

    protected ArrayList<String> mInterestListItems = new ArrayList<String>();

    protected ArrayAdapter<String> listAdapter;
    protected ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_profile);

        mImageView = (ImageView)findViewById(R.id.imageView);
        mImageView.setImageResource(R.drawable.fiplus);

        mInterestSpinner = (Spinner)findViewById(R.id.interests_spinner);

        mInterestListView = (ListView)findViewById(R.id.interests_list);
        mInterestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mInterestListItems.remove(position);
                listAdapter.notifyDataSetChanged();
            }
        });

        mAddTextView = (TextView)findViewById(R.id.add_interest_label);
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


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onAddClick(View view)
    {
        mInterestListItems.add(mInterestSpinner.getSelectedItem().toString());
        listAdapter.notifyDataSetChanged();
    }

}
