package model;

import com.wordnik.client.model.Location;

public class PendingLocItem {

    private String mText;
    private Location mLoc;

    public PendingLocItem(){}

    public PendingLocItem(String mText, Location mLoc){
        this.mText = mText;
        this.mLoc = mLoc;
    }

    public String getString() {
        return this.mText;
    }

    public Location getLoc() {
        return this.mLoc;
    }

}
