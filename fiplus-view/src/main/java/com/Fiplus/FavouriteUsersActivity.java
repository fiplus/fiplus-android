package com.Fiplus;
import android.app.Activity;
import android.os.Bundle;
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

        FavouriteUsersList.add(new FavouriteUsersListItem("Bob", R.drawable.fiplus));
        FavouriteUsersList.add(new FavouriteUsersListItem("Xerxes", R.drawable.fiplus));

        mFavouriteUsersListAdapter = new FavouriteUsersListAdapter(this, FavouriteUsersList);
        mFavouriteUsersList.setAdapter(mFavouriteUsersListAdapter);

    }

}
