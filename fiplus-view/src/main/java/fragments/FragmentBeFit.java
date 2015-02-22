package fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.Fiplus.R;
import com.Fiplus.ViewEventActivity;
import com.wordnik.client.api.MatchesApi;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Activity;
import com.wordnik.client.model.Location;
import com.wordnik.client.model.UserProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import adapters.EventListAdapter;
import model.EventListItem;
import utils.IAppConstants;
import utils.LocationUtil;
import utils.PrefUtil;

public class FragmentBeFit extends Fragment{

    private ProgressDialog progressDialog;

    public static final String TAG = FragmentBeFit.class
            .getSimpleName();

    private ListView mEventsList;
    private EventListAdapter mEventListAdapter ;

    public FragmentBeFit() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstancesState)
    {
        super.onCreate(savedInstancesState);
        progressDialog = ProgressDialog.show(getActivity(), "Getting events...", getString(R.string.progress_dialog_text), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_generic_list, container, false);
        mEventsList = (ListView) v.findViewById(R.id.eventsList);
        mEventsList.setOnItemClickListener(new EventItemClickListener());

        return v;
    }

    //TODO: Remove DUMMY EVENTS
    private void setEventList(List<Activity> activities)
    {
        if (activities == null)
            return;
        ArrayList<EventListItem> eventList = new ArrayList<>();

        for(int i = 0; i < activities.size(); i++)
            eventList.add(new EventListItem(
                    R.drawable.ic_configure,
                    activities.get(i).getName(),
                    LocationUtil.getLocationStrings(activities.get(i).getSuggested_locations(), getActivity().getBaseContext()),
                    activities.get(i).getSuggested_times(),
                    ((Integer)activities.get(i).getNum_attendees().intValue()).toString(),
                    activities.get(i).getActivity_id()));

        mEventListAdapter = new EventListAdapter(getActivity(), eventList, TAG);
        mEventsList.setAdapter(mEventListAdapter);

        mEventListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        GetEvents getEvents = new GetEvents();
        getEvents.execute();
    }

    protected class EventItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            String sEventID = mEventListAdapter.getItem(position).getEventId();
            Intent intent = new Intent(getActivity(), ViewEventActivity.class);
            intent.putExtra("eventID", sEventID);
            startActivity(intent);
        }
    }

    private class GetEvents extends AsyncTask<Void, Void, String>
    {
        protected List<Activity> response;

        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected String doInBackground(Void... params)
        {
            MatchesApi matchesApi = new MatchesApi();
            matchesApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            matchesApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try{
                UserProfile profile = usersApi.getUserProfile(PrefUtil.getString(getActivity().getApplicationContext(), IAppConstants.USER_ID));
                response = matchesApi.matchActivities(
                        50.0,
                        false,
                        10.0,
                        profile.getLocation());
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (response != null)
                setEventList(response);
            if (progressDialog != null)
            {
                progressDialog.dismiss();
            }
        }
    }
}
