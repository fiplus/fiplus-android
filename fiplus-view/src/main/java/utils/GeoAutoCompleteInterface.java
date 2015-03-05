package utils;

import android.widget.ArrayAdapter;

import com.wordnik.client.model.Location;

public interface GeoAutoCompleteInterface {
    static final int MAX_CHARS = 3; //max chars before suggesting locations
    static final int AUTOCOMPLETE = 5; //number of suggestions
    static final int FINAL_LOC = 1; //when adding the location
    final Integer MAX = 3;
    public void populateLocation(Location location, String address);
    public ArrayAdapter<String> getAutoComplete();
}
