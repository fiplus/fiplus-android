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

//import android.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNearYou extends Fragment {

    public static final String TAG = FragmentNearYou.class
            .getSimpleName();

    private ListView mEventsList;
    private EventListAdapter mEventListAdapter ;

    public FragmentNearYou() {
        // Required empty public constructor

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

        eventList.add(new EventListItem(R.drawable.ic_configure, "First Near You Event", "Saint John", "4:30PM", "4 Attendees"));
        eventList.add(new EventListItem(R.drawable.ic_activities, "Second Near You Event", "Calgary", "10:30PM", "4 Attendees"));

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
