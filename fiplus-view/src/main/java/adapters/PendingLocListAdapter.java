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

import model.PendingLocItem;
import utils.ListViewUtil;


public class PendingLocListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PendingLocItem> mPendingLocListItems;
    private ListView view;

    public PendingLocListAdapter(Context context, ArrayList<PendingLocItem> suggestionListItems, ListView view) {
        this.context = context;
        this.mPendingLocListItems = suggestionListItems;
        this.view = view;
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
                mPendingLocListItems.remove(position);
                notifyDataSetChanged();
                ListViewUtil.setListViewHeightBasedOnChildren(view);

                if(mPendingLocListItems.size() == 0)
                    view.setVisibility(View.GONE);
            }
        });
        mText.setText(mPendingLocListItems.get(position).getString());

        return convertView;
    }
}
