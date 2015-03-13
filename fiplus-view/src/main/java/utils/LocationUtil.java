package utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.wordnik.client.model.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationUtil {

    public static List<String> getLocationStrings(List<Location> locations, Context context)
    {
        Address addr;
        List<Address> addressList;
        List<String> locationStringsList = new ArrayList<String>();

        for(int i = 0; i < locations.size(); i++) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.CANADA);
                addressList = geocoder.getFromLocation(locations.get(i).getLatitude(),
                        locations.get(i).getLongitude(),
                        1);
                if (addressList != null && addressList.size() > 0) {
                    addr = addressList.get(0);
                    String addressText = String.format(
                            "%s, %s, %s",
                            // If there's a street address, add it
                            addr.getMaxAddressLineIndex() > 0 ?
                                    addr.getAddressLine(0) : "",
                            // Locality is usually a city
                            addr.getLocality() != null ? addr.getLocality() : "",
                            // The country of the address
                            addr.getCountryName());
                    // Return the text
                    locationStringsList.add(addressText);
                }
            } catch (IOException e) {
                Log.e("LocationUtil", e.getMessage());
            }
        }
        return locationStringsList;
    }

    public static String getLocationString(Location location, Context context)
    {
        Address addr;
        List<Address> addressList;

        try {
            Geocoder geocoder = new Geocoder(context, Locale.CANADA);
            addressList = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(),
                    1);
            if (addressList != null && addressList.size() > 0) {
                addr = addressList.get(0);
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        addr.getMaxAddressLineIndex() > 0 ?
                                addr.getAddressLine(0) : "",
                        // Locality is usually a city
                        addr.getLocality() != null ? addr.getLocality() : "",
                        // The country of the address
                        addr.getCountryName());
                // Return the text
                return addressText;
            }
        } catch (IOException e) {
            Log.e("LocationUtil ", e.getMessage());
        }
        return "<ERROR: Location not found>";
    }
}
