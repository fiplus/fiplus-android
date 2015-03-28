package adapters;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Fiplus.R;
import com.wordnik.client.api.ActsApi;

import java.util.ArrayList;

import model.EventListItem;
import utils.IAppConstants;
import utils.PrefUtil;

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
        Button eventButton;

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = mInflater.inflate(R.layout.item_events_whats_happening, parent, false);
            eventButton = (Button) convertView.findViewById(R.id.event_button);

            switch (currentClass)
            {
                case VIEWPROFILEACTIVITY :
                    eventButton.setVisibility(convertView.GONE);
                    break;
                case FRAGMENTMYEVENTS:
                    eventButton.setText(R.string.cancel_button);
                    eventButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CancelEvent cancelEvent = new CancelEvent(position);
                            cancelEvent.execute();
                        }
                    });
                    break;
                case FRAGMENTEVENTS:
                    eventButton.setText(R.string.rate_now);
                    break;
                default:
//                    eventButton.setText(R.string.join_button);
                    eventButton.setVisibility(convertView.GONE);
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

    private class CancelEvent extends AsyncTask<Void, Void, String>
    {
        protected int position;

        public CancelEvent(int position)
        {
            super();
            this.position = position;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            ActsApi actsApi = new ActsApi();
            actsApi.getInvoker().setContext(context);
            actsApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try{
                com.wordnik.client.model.Activity response = actsApi.getActivity(mEventItems.get(position).getEventId());
                if(!response.getCreator().equalsIgnoreCase(PrefUtil.getString(context, IAppConstants.USER_ID)))
                    actsApi.unjoinActivity(mEventItems.get(position).getEventId());
                else
                    actsApi.cancelActivity(mEventItems.get(position).getEventId());
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            mEventItems.remove(position);
            notifyDataSetChanged();
        }
    }

}