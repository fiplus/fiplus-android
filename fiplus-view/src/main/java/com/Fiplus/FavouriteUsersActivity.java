package com.Fiplus;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Favourites;

import java.util.ArrayList;

import adapters.FavouriteUsersListAdapter;
import model.FavouriteUsersListItem;
import utils.IAppConstants;

/**
 * Created by Allan on 03/12/2014.
 */
public class FavouriteUsersActivity extends Activity {

    private ListView mFavouriteUsersList;
    private FavouriteUsersListAdapter mFavouriteUsersListAdapter;

    @Override
    protected void onCreate(Bundle savedInstancesState) {
        Tracker t = ((FiplusApplication)this.getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());

        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_favourite_users);

        mFavouriteUsersList = (ListView) findViewById(R.id.favouriteUsersList);

        mFavouriteUsersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //TODO: Add item click listener for view profile list
                Intent intent = new Intent(getBaseContext(), ViewProfileActivity.class);
                intent.putExtra("userName", mFavouriteUsersListAdapter.getItem(position).getFavouriteUser());
                intent.putExtra("userProfile",  mFavouriteUsersListAdapter.getItem(position).getUserPic());
                intent.putExtra("userId", mFavouriteUsersListAdapter.getItem(position).getUserId());
                intent.putStringArrayListExtra("userInterest", (ArrayList<String>)mFavouriteUsersListAdapter.getItem(position).getTaggedInterests());
                intent.putExtra("favourited", true);
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

    @Override
    public void onResume() {
        super.onResume();
        GetFavorites getFavorites = new GetFavorites();
        getFavorites.execute();
    }

    private void setFavouriteUsersList(Favourites favourites) {
        ArrayList<FavouriteUsersListItem> FavouriteUsersList = new ArrayList<FavouriteUsersListItem>();

        for(int i = 0; i < favourites.getFavourite_users().size(); i++ )
        {
            FavouriteUsersList.add(new FavouriteUsersListItem(favourites.getFavourite_users().get(i).getUsername(),
                    R.mipmap.fiplus,
                    favourites.getFavourite_users().get(i).getUser_id(),
                    favourites.getFavourite_users().get(i).getTagged_interests()));
        }

        mFavouriteUsersListAdapter = new FavouriteUsersListAdapter(this, FavouriteUsersList);
        mFavouriteUsersList.setAdapter(mFavouriteUsersListAdapter);
    }

    class GetFavorites extends AsyncTask<Void, Void, String>
    {
        private Favourites favourites;

        @Override
        protected String doInBackground(Void... params) {
            UsersApi usersApi = new UsersApi();
            usersApi.getInvoker().setContext(getBaseContext());
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                favourites = usersApi.getFavourites(100.0);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            setFavouriteUsersList(favourites);
        }
    }
}
