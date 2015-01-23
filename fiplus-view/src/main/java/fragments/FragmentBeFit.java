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
import com.wordnik.client.api.ActivityApi;
import com.wordnik.client.api.MatchApi;
import com.wordnik.client.model.ActivitiesResponse;
import com.wordnik.client.model.Activity;
import com.wordnik.client.model.ActivityDetailResponse;

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
    private void setEventList(List<ActivityDetailResponse> activities)
    {
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
                    "Placeholder",
                    "Time placeholder",
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
        protected ActivitiesResponse response;

        @Override
        protected String doInBackground(Void... params)
        {
            MatchApi matchApi = new MatchApi();
            matchApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            matchApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try{
                response = matchApi.matchActivities(
                        PrefUtil.getString(getActivity().getApplicationContext(), IAppConstants.EMAIL),
                        10,
                        true,
                        0,
                        null);
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            setEventList(response.getActivities());
        }
    }
}
