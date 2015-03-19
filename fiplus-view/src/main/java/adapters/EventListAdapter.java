package adapters;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Fiplus.R;

import java.util.ArrayList;

import model.EventListItem;

public class EventListAdapter extends BaseAdapter
{
    enum Classes {
        VIEWPROFILEACTIVITY,
        FRAGMENTBEFIT,
        FRAGMENTNEARYOU,
        FRAGMENTINTEREST,
        FRAGMENTMYEVENTS,
        FRAGMENTEVENTS,
        FRAGMENTRECENTACTIVITIES
    }

    private Context context;
    private ArrayList<EventListItem> mEventItems;
    private String className;

    public EventListAdapter(Context context, ArrayList<EventListItem> mEventItems, String className)
    {
        this.context = context;
        this.mEventItems = mEventItems;
        this.className = className;
    }

    @Override
    public int getCount()
    {
        return mEventItems.size();
    }

    @Override
    public EventListItem getItem(int position)
    {
        return mEventItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        Classes currentClass = Classes.valueOf(className.toUpperCase());
        TextView confirmEvent;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.item_events_whats_happening, parent, false);
            confirmEvent = (TextView) convertView.findViewById(R.id.confirm_event);

            switch (currentClass)
            {
                case FRAGMENTMYEVENTS:
                    //possibly show firm up here
                default:
                    confirmEvent.setVisibility(convertView.GONE);
                    break;
            }

        }

        ImageView eventPic = (ImageView)convertView.findViewById(R.id.event_pic);
        TextView eventName = (TextView) convertView.findViewById(R.id.event_name);
        TextView eventLoc = (TextView) convertView.findViewById(R.id.event_location);
        TextView eventTime = (TextView) convertView.findViewById(R.id.event_time);
        TextView eventAttendee = (TextView) convertView.findViewById(R.id.event_attendees);

        eventPic.setImageResource(mEventItems.get(position).getEventPic());
        eventName.setText(mEventItems.get(position).getEventName());
        eventLoc.setText(mEventItems.get(position).getEventLocation());
        eventTime.setText(mEventItems.get(position).getEventTime());
        eventAttendee.setText(mEventItems.get(position).getEventAttendee());

        return convertView;
    }

}