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
    private int firstTime[];
    private boolean isCancelled;
    private boolean isFirmUp = false;
    private int selectedItem = -1;

    public SuggestionListAdapter(Context context, ArrayList<SuggestionListItem> suggestionListItems, boolean isCancelled) {
        this.context = context;
        this.mSuggestionListItems = suggestionListItems;
        firstTime = new int[suggestionListItems.size()];
        for(int i = 0; i < suggestionListItems.size(); i++)
            firstTime[i] = 0;
        this.isCancelled = isCancelled;
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
            if(firstTime[position] < 2) {
                sugCheckBox.setChecked(mSuggestionListItems.get(position).getYesVote());
                firstTime[position]++;
            }
            sugCheckBox.setText(mSuggestionListItems.get(position).getSuggestion());

            if(isCancelled)
            {
                sugCheckBox.setClickable(false);
                sugCheckBox.setEnabled(false);
                sugCheckBox.setFocusable(false);
            }
        }

        TextView voteProgress = (TextView) convertView.findViewById(R.id.vote_progress);
        voteProgress.setText("" + mSuggestionListItems.get(position).getVote() + " votes");

        return convertView;
    }
}
