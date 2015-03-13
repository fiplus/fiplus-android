package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.Fiplus.FiplusApplication;
import com.Fiplus.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

import adapters.EventListAdapter;
import model.EventListItem;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEvents extends Fragment {

    public static final String TAG = FragmentEvents.class.getSimpleName();

    private ListView mEventsList;
    private EventListAdapter mEventListAdapter ;

    public FragmentEvents() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Tracker t = ((FiplusApplication)getActivity().getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_generic_list, container, false);
        mEventsList = (ListView) v.findViewById(R.id.eventsList);

        setEventList();
        mEventsList.setOnItemClickListener(new EventItemClickListener());
        return v;
    }

    //TODO: Remove DUMMY EVENTS
    private void setEventList()
    {
        ArrayList<EventListItem> eventList = new ArrayList<EventListItem>();

        //eventList.add(new EventListItem(R.drawable.ic_configure, "My Recent Event 1", "Saint John", "4:30PM", "4 Attendees"));
        //eventList.add(new EventListItem(R.drawable.ic_activities, "My Recent Event 2", "Calgary", "10:30PM", "4 Attendees"));

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

}
