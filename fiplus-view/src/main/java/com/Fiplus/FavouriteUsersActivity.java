package com.Fiplus;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import adapters.FavouriteUsersListAdapter;
import model.FavouriteUsersListItem;

/**
 * Created by Allan on 03/12/2014.
 */
public class FavouriteUsersActivity extends Activity {

    private ListView mFavouriteUsersList;
    private FavouriteUsersListAdapter mFavouriteUsersListAdapter;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_favourite_users);

        mFavouriteUsersList = (ListView) findViewById(R.id.favouriteUsersList);
        setFavouriteUsersList();

        mFavouriteUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //TODO: Add item click listener for view profile list
                Intent intent = new Intent(getBaseContext(), ViewProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                startActivity(intent);
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

    private void setFavouriteUsersList() {
        ArrayList<FavouriteUsersListItem> FavouriteUsersList = new ArrayList<FavouriteUsersListItem>();

        FavouriteUsersList.add(new FavouriteUsersListItem("John Doe", R.drawable.fiplus));
        FavouriteUsersList.add(new FavouriteUsersListItem("Xerxes", R.drawable.fiplus));

        mFavouriteUsersListAdapter = new FavouriteUsersListAdapter(this, FavouriteUsersList);
        mFavouriteUsersList.setAdapter(mFavouriteUsersListAdapter);

    }

}
