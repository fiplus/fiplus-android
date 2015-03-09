package model;

import com.wordnik.client.model.Time;

public class PendingTimeItem {

    private String mText;
    private Time mTime;

    public PendingTimeItem(){}

    public PendingTimeItem(String mText, Time mTime){
        this.mText = mText;
        this.mTime = mTime;
    }

    public String getString() {
        return this.mText;
    }

    public Time getTime() {
        return this.mTime;
    }
}
