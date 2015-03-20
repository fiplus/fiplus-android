package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Fiplus.R;

import java.util.ArrayList;

import model.SuggestionListItem;

public class SuggestionListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SuggestionListItem> mSuggestionListItems;
    private boolean checked[];

    public SuggestionListAdapter(Context context, ArrayList<SuggestionListItem> suggestionListItems) {
        this.context = context;
        this.mSuggestionListItems = suggestionListItems;
        checked = new boolean[suggestionListItems.size()];
        for(int i = 0; i < suggestionListItems.size(); i++)
            checked[i] = suggestionListItems.get(i).getYesVote();
    }

    @Override
    public int getCount() {
        return mSuggestionListItems.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.item_suggestion, parent, false);
        }

        CheckBox sugCheckBox = (CheckBox) convertView.findViewById(R.id.suggestion_checkbox);
        sugCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked[position] = isChecked;
            }
        });
        sugCheckBox.setChecked(checked[position]);
        TextView voteProgress = (TextView) convertView.findViewById(R.id.vote_progress);

        sugCheckBox.setText(mSuggestionListItems.get(position).getSuggestion());
        voteProgress.setText("" + mSuggestionListItems.get(position).getVote() + " votes");

        return convertView;
    }
}
