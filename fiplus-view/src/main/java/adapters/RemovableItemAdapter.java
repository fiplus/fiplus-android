package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Fiplus.R;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.Time;

import java.util.ArrayList;
import java.util.List;

import model.PendingTimeItem;
import utils.ListViewUtil;

/**
 * Created by Javier on 3/19/2015.
 */
public class RemovableItemAdapter extends BaseAdapter {
    private Context context;
    private List<String> mRemovableListItems;
    private List<Location> mLocationItems;
    private List<Time> mTimeItems;
    private ListView view;

    public RemovableItemAdapter(Context context, List<String> listItems, ListView view) {
        this.context = context;
        this.mRemovableListItems = listItems;
        this.view = view;
    }

    public RemovableItemAdapter(Context context, List<String> listItems, List<Location> locationItems, ListView view) {
        this.context = context;
        this.mRemovableListItems = listItems;
        this.mLocationItems = locationItems;
        this.view = view;
    }

    public RemovableItemAdapter(Context context, List<String> listItems, ListView view, List<Time> timeItems) {
        this.context = context;
        this.mRemovableListItems = listItems;
        this.mTimeItems = timeItems;
        this.view = view;
    }

    @Override
    public int getCount() {
        return mRemovableListItems.size();
    }

    @Override
    public String getItem(int position) {
        return mRemovableListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_removable_item, parent, false);
        }

        TextView mText = (TextView) convertView.findViewById(R.id.removable_item);
        ImageView mIcon = (ImageView) convertView.findViewById(R.id.remove_item);
        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemovableListItems.remove(position);
                if(mLocationItems != null)
                    mLocationItems.remove(position);
                if(mTimeItems != null)
                    mTimeItems.remove(position);

                if (mRemovableListItems.size() == 0)
                    view.setVisibility(View.GONE);

                ListViewUtil.setListViewHeightBasedOnChildren(view);
                notifyDataSetChanged();

            }
        });
        mText.setText(mRemovableListItems.get(position));
        notifyDataSetChanged();
        return convertView;
    }
}
