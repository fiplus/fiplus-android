package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Fiplus.R;

import java.util.ArrayList;

import model.FavouriteUsersListItem;

/**
 * Created by Allan on 03/12/2014.
 */
public class FavouriteUsersListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FavouriteUsersListItem> mFavouriteUserListItems;

    public FavouriteUsersListAdapter(Context context, ArrayList<FavouriteUsersListItem> mFavouriteUserListItems) {
        this.context = context;
        this.mFavouriteUserListItems = mFavouriteUserListItems;
    }

    @Override
    public int getCount() {
        return mFavouriteUserListItems.size();
    }

    @Override
    public FavouriteUsersListItem getItem(int position) {
        return mFavouriteUserListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_favourite_users_list, parent, false);
        }

        ImageView favouritePic = (ImageView) convertView.findViewById(R.id.favourite_users_pic);
        TextView favouriteName = (TextView) convertView.findViewById(R.id.favourite_users_name);

        favouritePic.setImageResource(mFavouriteUserListItems.get(position).getUserPic());
        favouriteName.setText(mFavouriteUserListItems.get(position).getFavouriteUser());

        return convertView;
    }
}