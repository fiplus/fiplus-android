package fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.Fiplus.R;
import com.wordnik.client.api.MatchesApi;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Activity;
import com.wordnik.client.model.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

import adapters.EventListAdapter;
import model.EventListItem;
import utils.IAppConstants;
import utils.PrefUtil;

/**
 * Created by jsfirme on 14-12-03.
 */
public class FragmentBeFit extends Fragment{

    public static final String TAG = FragmentBeFit.class
            .getSimpleName();

    private ListView mEventsList;
    private EventListAdapter mEventListAdapter ;

    public FragmentBeFit() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_generic_list, container, false);
        mEventsList = (ListView) v.findViewById(R.id.eventsList);
        mEventsList.setOnItemClickListener(new EventItemClickListener());

        GetEvents getEvents = new GetEvents();
        getEvents.execute();
        return v;
    }

    //TODO: Remove DUMMY EVENTS
    private void setEventList(List<Activity> activities)
    {
        if (activities == null)
            return;
        ArrayList<EventListItem> eventList = new ArrayList<EventListItem>();
//
//        eventList.add(new EventListItem(R.drawable.ic_configure, "First Be Fi+! Event", "Calgary", "4:30PM", "10 Attendees"));
//        eventList.add(new EventListItem(R.drawable.ic_help, "Second Be Fi+! Event", "Calgary", "10:30PM", "11 Attendees"));
//        eventList.add(new EventListItem(R.drawable.ic_configure, "Third Be Fi+! Event", "Toronto", "10:30PM", "2 Attendees"));
//        eventList.add(new EventListItem(R.drawable.ic_configure, "Fourth Be Fi+! Event", "Calgary", "10:30PM", "11 Attendees"));
//        eventList.add(new EventListItem(R.drawable.ic_configure, "Fifth Be Fi+! Event", "Calgary", "10:30PM", "11 Attendees"));
//
        for(int i = 0; i < activities.size(); i++)
            eventList.add(new EventListItem(
                    R.drawable.ic_configure,
                    activities.get(i).getName(),
                    activities.get(i).getSuggested_locations(),
                    activities.get(i).getSuggested_times(),
                    activities.get(i).getMax_attendees().toString()));

        mEventListAdapter = new EventListAdapter(getActivity(), eventList, TAG);
        mEventsList.setAdapter(mEventListAdapter);

    }

    protected class EventItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            // TODO: Put implementation
        }
    }

    private class GetEvents extends AsyncTask<Void, Void, String>
    {
        protected List<Activity> response;

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
                UserProfile profile = usersApi.getUserProfile(PrefUtil.getString(getActivity().getApplicationContext(), IAppConstants.EMAIL, null));
                response = matchesApi.matchActivities(
                        PrefUtil.getString(getActivity().getApplicationContext(), IAppConstants.EMAIL),
                        10.0,
                        true,
                        1.0,
                        profile.getLocation());
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            setEventList(response);
        }
    }
}
