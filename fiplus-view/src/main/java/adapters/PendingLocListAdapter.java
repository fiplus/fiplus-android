package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Fiplus.R;

import java.util.ArrayList;

import model.PendingLocItem;


public class PendingLocListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PendingLocItem> mPendingLocListItems;

    public PendingLocListAdapter(Context context, ArrayList<PendingLocItem> suggestionListItems) {
        this.context = context;
        this.mPendingLocListItems = suggestionListItems;
    }

    @Override
    public int getCount() {
        return mPendingLocListItems.size();
    }

    @Override
    public PendingLocItem getItem(int position) {
        return mPendingLocListItems.get(position);
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
            convertView = mInflater.inflate(R.layout.item_pending_suggestion, parent, false);
        }

        TextView mText = (TextView) convertView.findViewById(R.id.pending_suggestion);
        mText.setText(mPendingLocListItems.get(position).getString());

        return convertView;
    }
}
