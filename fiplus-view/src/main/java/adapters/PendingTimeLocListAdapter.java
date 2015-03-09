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

import model.PendingTimeItem;

public class PendingTimeLocListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PendingTimeItem> mPendingTimeListItems;

    public PendingTimeLocListAdapter(Context context, ArrayList<PendingTimeItem> suggestionListItems) {
        this.context = context;
        this.mPendingTimeListItems = suggestionListItems;
    }

    @Override
    public int getCount() {
        return mPendingTimeListItems.size();
    }

    @Override
    public PendingTimeItem getItem(int position) {
        return mPendingTimeListItems.get(position);
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
        mText.setText(mPendingTimeListItems.get(position).getString());

        return convertView;
    }
}
