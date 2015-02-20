package utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.Fiplus.CreateEventActivity;
import com.wordnik.client.model.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingLocation extends AsyncTask<String, Void, String>
{
    //ProgressDialog progressDialog;
    Context mContext;
    List<Address> addressList = null;
    Address addr;
    Location location = new Location();
    CreateEventActivity callerActivity;

    public GeocodingLocation(Context context, CreateEventActivity activity) {
        super();
        mContext = context;
        callerActivity = activity;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        //progressDialog= ProgressDialog.show(CreateEventActivity.this, getString(R.string.progress_dialog_title) + "...", getString(R.string.progress_dialog_text), true);
    }

    @Override
    protected String doInBackground(String... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.CANADA);

        // Get the current location from the input parameter list
        String loc = params[0];

        int i = 0;
        try {
            //try 3 times in case geocoder doesn't work the first time
            do {
                addressList = geocoder.getFromLocationName(loc, 1);
                i++;
            } while (addressList.size()==0 && i < 3);

        } catch (IOException e1) {
            Log.e("LocationSampleActivity",
                    "IO Exception in getFromLocation()");
            e1.printStackTrace();
        }

        // If the geocode returned an address
        if (addressList != null && addressList.size() > 0) {

            addr = addressList.get(0);
            location.setLatitude(addr.getLatitude());
            location.setLongitude(addr.getLongitude());

                /*
                 * Format the first line of address (if available),
                 * city (if available), and country name.
                 */
            String addressText = String.format(
                    "%s, %s, %s %s",
                    // If there's a street address, add it
                    addr.getMaxAddressLineIndex() > 0 ?
                            addr.getAddressLine(0) : "",
                    // Locality is usually a city
                    addr.getLocality() != null ? addr.getLocality() : "",
                    // The country of the address
                    addr.getCountryName(),
                    // If there's a postal code, add it
                    addr.getPostalCode() != null ? addr.getPostalCode() : "");
            // Return the text
            return addressText;
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(String address) {

        super.onPostExecute(address);
        callerActivity.populateLocation(location, address);
    }
}
