package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.Fiplus.R;

import java.util.ArrayList;

import adapters.EventListAdapter;
import model.EventListItem;


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

        setEventList();
        mEventsList.setOnItemClickListener(new EventItemClickListener());

        return v;
    }

    //TODO: Remove DUMMY EVENTS
    private void setEventList()
    {
        ArrayList<EventListItem> eventList = new ArrayList<EventListItem>();

        eventList.add(new EventListItem(R.drawable.ic_configure, "My Event 1", "Saint John", "4:30PM", "4 Attendees"));
        eventList.add(new EventListItem(R.drawable.ic_activities, "My Event 2", "Calgary", "10:30PM", "4 Attendees"));

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
