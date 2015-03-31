package utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.wordnik.client.model.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingLocation extends AsyncTask<String, Void, List<Address>>
{
    //ProgressDialog progressDialog;
    Context mContext;
    List<Address> addressList = null;
    Address addr;
    Location location = new Location();
    GeoAutoCompleteInterface callerActivity;
    int max_location;

    public GeocodingLocation(Context context, GeoAutoCompleteInterface activity, int num) {
        super();
        mContext = context;
        callerActivity = activity;
        max_location = num;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        //progressDialog= ProgressDialog.show(CreateEventActivity.this, getString(R.string.progress_dialog_title) + "...", getString(R.string.progress_dialog_text), true);
    }

    @Override
    protected List<Address> doInBackground(String... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.CANADA);

        // Get the current location from the input parameter list
        String loc = params[0];

        int i = 0;
        try {
            //try 3 times in case geocoder doesn't work the first time
            do {
                addressList = geocoder.getFromLocationName(loc, max_location);
                i++;
            } while (addressList.size()==0 && i < 3);

        } catch (IOException e1) {
            Log.e("LocationSampleActivity",
                    "IO Exception in getFromLocation()");
            e1.printStackTrace();
        }

        // If adding the final address (not from autocomplete)
        if (addressList != null && addressList.size() > 0 && max_location == 1) {

            addr = addressList.get(0);
            location.setLatitude(addr.getLatitude());
            location.setLongitude(addr.getLongitude());

            //only have one address
            addressList.clear();
            addressList.add(addr);
        }

        return addressList;
    }

    @Override
    protected void onPostExecute(List<Address> address) {

        String temp = null;
        super.onPostExecute(address);

        if(max_location == 1 )
        {
            if(addressList != null && addressList.size() > 0)
            {
                Address a = address.get(0);
                temp = (a.getMaxAddressLineIndex() > 0 ? a.getAddressLine(0) : "")+" "+
                        (a.getLocality() != null ? a.getLocality() : "")
                        +" "+(a.getCountryName() != null ? a.getCountryName() : "");
                location.setAddress(temp);
            }

            callerActivity.populateLocation(location, temp);
        }
        else
        {
            callerActivity.getAutoComplete().clear();

            if(addressList != null)
            {
                for (Address a : addressList) {
                    //String temp = ""+ a.getFeatureName()+" "+a.getCountryName()+" "+a.getPostalCode();
                    temp = (a.getMaxAddressLineIndex() > 0 ? a.getAddressLine(0) : "")+" "+
                            (a.getLocality() != null ? a.getLocality() : "")
                            +" "+(a.getCountryName() != null ? a.getCountryName() : "");
                    callerActivity.getAutoComplete().add(temp);
                }
            }

            callerActivity.getAutoComplete().notifyDataSetChanged();
        }
    }
}
