package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Fiplus.R;

import java.util.ArrayList;

import model.PendingTimeItem;
import utils.ListViewUtil;

public class PendingTimeLocListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PendingTimeItem> mPendingTimeListItems;
    private ListView view;

    public PendingTimeLocListAdapter(Context context, ArrayList<PendingTimeItem> suggestionListItems, ListView view) {
        this.context = context;
        this.mPendingTimeListItems = suggestionListItems;
        this.view = view;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_pending_suggestion, parent, false);
        }

        TextView mText = (TextView) convertView.findViewById(R.id.pending_suggestion);
        ImageView mIcon = (ImageView) convertView.findViewById(R.id.delete_pending);
        mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPendingTimeListItems.remove(position);
                notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(view);

                if(mPendingTimeListItems.size() == 0)
                    view.setVisibility(View.GONE);
            }
        });
        mText.setText(mPendingTimeListItems.get(position).getString());

        return convertView;
    }
}
