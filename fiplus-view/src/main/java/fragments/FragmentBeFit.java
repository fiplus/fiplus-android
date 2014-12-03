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

/**
 * Created by jsfirme on 14-12-03.
 */
public class FragmentBeFit extends Fragment{

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
        setEventList();
        mEventsList.setOnItemClickListener(new EventItemClickListener());

        return v;
    }

    //TODO: Remove DUMMY EVENTS
    private void setEventList()
    {
        ArrayList<EventListItem> eventList = new ArrayList<EventListItem>();

        eventList.add(new EventListItem(R.drawable.ic_configure, "First Be Fi+! Event", "Calgary", "4:30PM", "10 Attendees"));
        eventList.add(new EventListItem(R.drawable.ic_help, "Second Be Fi+! Event", "Calgary", "10:30PM", "11 Attendees"));
        eventList.add(new EventListItem(R.drawable.ic_configure, "Third Be Fi+! Event", "Toronto", "10:30PM", "2 Attendees"));
        eventList.add(new EventListItem(R.drawable.ic_configure, "Fourth Be Fi+! Event", "Calgary", "10:30PM", "11 Attendees"));
        eventList.add(new EventListItem(R.drawable.ic_configure, "Fifth Be Fi+! Event", "Calgary", "10:30PM", "11 Attendees"));

        mEventListAdapter = new EventListAdapter(getActivity(), eventList);
        mEventsList.setAdapter(mEventListAdapter);

    }

    protected class EventItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {

        }
    }
}
