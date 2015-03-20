package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.Fiplus.R;

import java.util.ArrayList;

import model.SuggestionListItem;
import utils.FirmUpDialog;

public class SuggestionListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SuggestionListItem> mSuggestionListItems;
    private boolean isCancelled;
    private boolean isFirmUp = false;
    private boolean isConfirmed = false;
    private int selectedItem = -1;

    public SuggestionListAdapter(Context context, ArrayList<SuggestionListItem> suggestionListItems, boolean isCancelled, boolean isConfirmed) {
        this.context = context;
        this.mSuggestionListItems = suggestionListItems;
        this.isCancelled = isCancelled;
        this.isConfirmed = isConfirmed;
    }

    //for firm up
    public SuggestionListAdapter(Context context, ArrayList<SuggestionListItem> suggestionListItems) {
        this.context = context;
        this.mSuggestionListItems = suggestionListItems;
        isFirmUp = true;
    }

    @Override
    public int getCount() {
        return mSuggestionListItems.size();
    }

    public int getSelectedItem()
    {
        return selectedItem;
    }

    @Override
    public SuggestionListItem getItem(int position) {
        return mSuggestionListItems.get(position);
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

            if(isFirmUp)
            {
                convertView = mInflater.inflate(R.layout.firmup_suggestion, parent, false);
            }
            else
            {
                convertView = mInflater.inflate(R.layout.item_suggestion, parent, false);
            }
        }

        TextView voteProgress = (TextView) convertView.findViewById(R.id.vote_progress);
        voteProgress.setText("" + mSuggestionListItems.get(position).getVote() + " votes");

        if(isFirmUp)
        {
            CheckedTextView firmUpBox = (CheckedTextView) convertView.findViewById(R.id.suggestion_radio);
            firmUpBox.setText(mSuggestionListItems.get(position).getSuggestion());

            if (position == FirmUpDialog.selectedIndex)// || position == FirmUpDialog.selectedIndexTime) {
            {
                selectedItem = position;
                firmUpBox.setChecked(true);
            } else {
                firmUpBox.setChecked(false);
            }
        }
        else
        {
            CheckBox sugCheckBox = (CheckBox) convertView.findViewById(R.id.suggestion_checkbox);
            sugCheckBox.setChecked(mSuggestionListItems.get(position).getYesVote());
            sugCheckBox.setText(mSuggestionListItems.get(position).getSuggestion());

            if(isCancelled)
            {
                sugCheckBox.setClickable(false);
                sugCheckBox.setEnabled(false);
                sugCheckBox.setFocusable(false);
            }
            else if(isConfirmed)
            {
                sugCheckBox.setClickable(false);
                sugCheckBox.setChecked(true);
                voteProgress.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
