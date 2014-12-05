package model;

public class EventListItem {

    // TODO: EventListItem
    private String mEventName;
    private int mEventPic;
    private String mEventLocation;
    private String mEventTime;
    private String mEventAttendee;


    public EventListItem(){}

    public EventListItem(int mEventPic, String mEventName, String mEventLocation, String mEventTime, String mEventAttendee)
    {
        this.mEventName = mEventName;
        this.mEventPic = mEventPic;
        this.mEventLocation = mEventLocation;
        this.mEventTime = mEventTime;
        this.mEventAttendee = mEventAttendee;
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

}
