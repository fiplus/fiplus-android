package utils;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.Fiplus.R;
import com.Fiplus.ViewEventActivity;
import com.wordnik.client.api.ActsApi;

import java.util.ArrayList;

import adapters.SuggestionListAdapter;
import model.SuggestionListItem;

public class FirmUpDialog {

    private ArrayList<SuggestionListItem> locSuggestionList;
    private ArrayList<SuggestionListItem> timeSuggestionList;
    private Context callingContext;
    private Dialog mFirmUP;
    private SuggestionListAdapter mTimesListAdapter;
    private SuggestionListAdapter mLocationListAdapter;
    private boolean isACreator;
    private String sEventID;
    private ListView mLocList;
    private ListView mTimeList;

    private Button firmUpButton;
    private Button cancelFirmUpButton;
    public static int selectedIndex;

    private Class<?> callingClass;

    public FirmUpDialog(Context c, String sEventID, ArrayList<SuggestionListItem> loc, ArrayList<SuggestionListItem> time, boolean isACreator)
    {
        callingContext = c;
        this.sEventID = sEventID;
        locSuggestionList = loc;
        timeSuggestionList = time;
        this.isACreator = isACreator;
        callingClass = c.getClass();
        selectedIndex = 0;
    }

    public void showFirmUpRsvp()
    {
        if(callingContext == null)
            System.out.print("LAME");
        mFirmUP = new Dialog(callingContext);
        mFirmUP.setContentView(R.layout.firm_up_layout);

        firmUpButton = (Button) mFirmUP.findViewById(R.id.firm_up_button);
        firmUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = mLocationListAdapter.getSelectedItem();
                int j = mTimesListAdapter.getSelectedItem();

                Log.e("Addr", String.valueOf(i));
                Log.e("Time", String.valueOf(j));

                FirmUpTask task = new FirmUpTask(locSuggestionList.get(i).getSuggestionId(), timeSuggestionList.get(j).getSuggestionId());
                task.execute();
            }
        });

        cancelFirmUpButton = (Button) mFirmUP.findViewById(R.id.cancel_firm_up);
        cancelFirmUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirmUP.dismiss();
            }
        });

        setup();
        addSuggestions();

        mFirmUP.show();
    }

    private void setup()
    {
        if(isACreator)
        {
            mFirmUP.setTitle(R.string.view_event_firm_up);
            firmUpButton.setText(R.string.view_event_firm_up_button);
        }
        else //it should never go to this code.
        {
            mFirmUP.setTitle(R.string.view_event_rsvp_button);
            firmUpButton.setText(R.string.view_event_rsvp_button);
        }
    }

    private void addSuggestions()
    {

        mLocList = (ListView) mFirmUP.findViewById(R.id.firm_up_location);

        mLocationListAdapter = new SuggestionListAdapter(callingContext, locSuggestionList);
        mLocList.setAdapter(mLocationListAdapter);
        ListViewUtil.setListViewHeightBasedOnChildren(mLocList);
        mLocList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                selectedIndex = position;
                mLocationListAdapter.notifyDataSetChanged();

            }
        });


        mTimeList = (ListView) mFirmUP.findViewById(R.id.firm_up_time);
        mTimeList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mTimesListAdapter = new SuggestionListAdapter(callingContext, timeSuggestionList);
        mTimeList.setAdapter(mTimesListAdapter);
        ListViewUtil.setListViewHeightBasedOnChildren(mTimeList);
        mTimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                selectedIndex = position;
                mTimesListAdapter.notifyDataSetChanged();

            }
        });

    }

    class FirmUpTask extends AsyncTask<Void, Void, String>
    {
        ActsApi setEventApi;
        String response = null;
        String firmUpLoc;
        String firmUpTime;

        public FirmUpTask(String loc, String time)
        {
            firmUpLoc = loc;
            firmUpTime = time;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            setEventApi = new ActsApi();
            setEventApi.getInvoker().setContext(callingContext);
            setEventApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try
            {
                setEventApi.firmUpSuggestion(sEventID, firmUpLoc);
                setEventApi.firmUpSuggestion(sEventID, firmUpTime);
            }
            catch (Exception e) {
                response = e.getMessage();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String message)
        {
            mFirmUP.dismiss();
            if(message == null)
            {
                Toast.makeText(callingContext, "Successfully firmed up event!", Toast.LENGTH_SHORT).show();
                ViewEventActivity activity = (ViewEventActivity)callingContext;
                activity.recreate();
            }
            else
            {
                Toast.makeText(callingContext, message, Toast.LENGTH_SHORT).show();
            }

        }
    }


}
