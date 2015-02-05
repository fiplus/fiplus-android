package model;

import com.wordnik.client.model.Location;
import com.wordnik.client.model.Time;

import java.util.List;

public class EventListItem {

    // TODO: EventListItem
    private String mEventName;
    private int mEventPic;
    private String mEventLocation;
    private String mEventTime;
    private String mEventAttendee;
    private String mEventId;


    public EventListItem(){}

    public EventListItem(int mEventPic, String mEventName, List<Location> eventLocations, List<Time> eventTimes, String mEventAttendee, String mEventId)
    {
        this.mEventName = mEventName;
        this.mEventId = mEventId;
        this.mEventPic = mEventPic;
        this.mEventAttendee = mEventAttendee + " people going!";
        if(eventLocations.size() == 1)
            mEventLocation = eventLocations.get(0).toString();
        else
            mEventLocation = "Multiple Locations";

        if(eventTimes.size() == 1)
            mEventTime = eventTimes.get(0).toString();
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

}
