package model;

import com.wordnik.client.model.Time;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class EventListItem {

    static final String DATEFORMAT = "MMM-dd-yyyy HH:mm a";

    // TODO: EventListItem
    private String mEventName;
    private int mEventPic;
    private String mEventLocation;
    private String mEventTime;
    private String mEventAttendee;
    private String mEventId;


    public EventListItem(){}

    public EventListItem(int mEventPic, String mEventName, List<String> eventLocations, List<Time> eventTimes, String mEventAttendee, String mEventId)
    {
        this.mEventName = mEventName;
        this.mEventId = mEventId;
        this.mEventPic = mEventPic;
        this.mEventAttendee = mEventAttendee + " people going!";
        if(eventLocations.size() == 1)
            mEventLocation = eventLocations.get(0);
        else
            mEventLocation = "Multiple Locations";

        if(eventTimes.size() == 1)
            mEventTime = getStartTime(eventTimes.get(0));
        else
            mEventTime = "Multiple Times";
    }

    public String getEventName()
    {
        return this.mEventName;
    }

    public int getEventPic()
    {
        return this.mEventPic;
    }

    public String getEventLocation()
    {
        return this.mEventLocation;
    }

    public String getEventTime()
    {
        return this.mEventTime;
    }

    public String getEventAttendee()
    {
        return this.mEventAttendee;
    }

    public String getEventId() {return this.mEventId; }

    private String getStartTime(Time time)
    {
        PrettyTime t1 = new PrettyTime(new Date());
        long startDate = time.getStart().longValue();
        Date d1 = new Date(startDate);
        return t1.format(d1);
    }
}
