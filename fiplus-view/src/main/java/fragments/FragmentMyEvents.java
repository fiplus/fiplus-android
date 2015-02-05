package fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
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
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Activity;

import java.util.ArrayList;
import java.util.List;

import adapters.EventListAdapter;
import model.EventListItem;
import utils.AlertFragmentDialog;
import utils.IAppConstants;
import utils.LocationUtil;


public class FragmentMyEvents extends Fragment {
    public static final String TAG = FragmentMyEvents.class.getSimpleName();

    private ListView mEventsList;
    private EventListAdapter mEventListAdapter ;

    public static FragmentMyEvents newInstance() {
        return new FragmentMyEvents();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_generic_list, container, false);
        mEventsList = (ListView) v.findViewById(R.id.eventsList);
        mEventsList.setOnItemClickListener(new EventItemClickListener());
        GetJoinedEvents getJoinedEvents = new GetJoinedEvents();
        getJoinedEvents.execute();
        return v;
    }

    //TODO: Remove DUMMY EVENTS
    private void setEventList(List<Activity> activities)
    {
        if (activities == null)
        {
            return;
        }
        else if(activities.size() == 0)
        {
            FragmentManager fm = getActivity().getFragmentManager();
            DialogFragment alertDialog = AlertFragmentDialog.newInstance("Be Fi+!");
            alertDialog.setCancelable(false);
            alertDialog.show(fm, "fragment_alert");
        }

        ArrayList<EventListItem> eventList = new ArrayList<>();

        for(int i = 0; i < activities.size(); i++)
            eventList.add(new EventListItem(
                    R.drawable.ic_configure,
                    activities.get(i).getName(),
                    LocationUtil.getLocationStrings(activities.get(i).getSuggested_locations(), getActivity().getBaseContext()),
                    activities.get(i).getSuggested_times(),
                    ((Integer)activities.get(i).getMax_attendees().intValue()).toString(),
                    activities.get(i).getActivity_id()));

        mEventListAdapter = new EventListAdapter(getActivity(), eventList, TAG);
        mEventsList.setAdapter(mEventListAdapter);
        mEventListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        GetJoinedEvents getJoinedEvents = new GetJoinedEvents();
        getJoinedEvents.execute();
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

    private class GetJoinedEvents extends AsyncTask<Void, Void, String>
    {
        protected List<Activity> response;
        protected ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog= ProgressDialog.show(getActivity(), getString(R.string.view_event_progress_bar_title) + "s...", getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try{
                response = usersApi.getActivities(false, true);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            progressDialog.dismiss();
            if (response != null)
                setEventList(response);
        }
    }
}
