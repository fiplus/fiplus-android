package fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.Fiplus.FiplusApplication;
import com.Fiplus.R;
import com.Fiplus.ViewEventActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wordnik.client.ApiException;
import com.wordnik.client.ApiInvoker;
import com.wordnik.client.api.MatchesApi;
import com.wordnik.client.api.UsersApi;
import com.wordnik.client.model.Activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import adapters.EventListAdapter;
import model.EventListItem;
import utils.IAppConstants;
import utils.LocationUtil;
import utils.PrefUtil;

public class FragmentBeFit extends Fragment{

    private ProgressDialog progressDialog;

    public static final String TAG = FragmentBeFit.class
            .getSimpleName();

    private ListView mEventsList;
    private EventListAdapter mEventListAdapter ;
    private ProgressBar spinner;

    public FragmentBeFit() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstancesState)
    {
        super.onCreate(savedInstancesState);
        Tracker t = ((FiplusApplication)getActivity().getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());
        progressDialog = ProgressDialog.show(getActivity(), "Getting events...", getString(R.string.progress_dialog_text), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_generic_list, container, false);
        mEventsList = (ListView) v.findViewById(R.id.eventsList);
        mEventsList.setOnItemClickListener(new EventItemClickListener());

        spinner = (ProgressBar)v.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);

        return v;
    }

    //TODO: Remove DUMMY EVENTS
    private void setEventList(List<Activity> activities)
    {
        if (activities == null)
            return;
        ArrayList<EventListItem> eventList = new ArrayList<EventListItem>();

        for(int i = 0; i < activities.size(); i++)
            eventList.add(new EventListItem(
                    R.drawable.ic_configure,
                    activities.get(i).getName(),
                    LocationUtil.getLocationStrings(activities.get(i).getLocations(), getActivity().getBaseContext()),
                    activities.get(i).getTimes(),
                    ((Integer)activities.get(i).getNum_attendees().intValue()).toString(),
                    activities.get(i).getActivity_id()));

        mEventListAdapter = new EventListAdapter(getActivity(), eventList, TAG);
        mEventsList.setAdapter(mEventListAdapter);

        mEventListAdapter.notifyDataSetChanged();
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        GetEvents getEvents = new GetEvents();
        getEvents.execute();
    }

    protected class EventItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            String sEventID = mEventListAdapter.getItem(position).getEventId();
            Intent intent = new Intent(getActivity(), ViewEventActivity.class);
            intent.putExtra("eventID", sEventID);
            startActivity(intent);
        }
    }

    private class GetEvents extends AsyncTask<Void, Void, String>
    {
        protected List<Activity> response;

        @Override
        protected void onPreExecute()
        {
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            if(PrefUtil.getBoolean(getActivity(), IAppConstants.BEFIT_CACHE_VALID_FLAG, false)
                    && (System.currentTimeMillis() - PrefUtil.getLong(getActivity(),IAppConstants.BEFIT_CACHE_UPDATE_VALUE)) < IAppConstants.BEFIT_CACHE_VALID_TIME)
            {
                try
                {
                    File cacheFile = new File(getActivity().getCacheDir() + "/" + GetEvents.class.getSimpleName());
                    FileInputStream cacheIn = new FileInputStream(cacheFile);
                    ByteArrayOutputStream cacheBytes = new ByteArrayOutputStream();
                    byte[] buffer = new byte[cacheIn.available()];
                    while(cacheIn.available() != 0)
                    {
                        cacheIn.read(buffer);
                        cacheBytes.write(buffer);
                        buffer = new byte[cacheIn.available()];
                    }
                    String cachedJson = new String(cacheBytes.toByteArray(), "UTF-8");
                    response = (List<Activity>)ApiInvoker.deserialize(cachedJson, "List", Activity.class);
                    cacheBytes.close();
                    cacheIn.close();
                    return null;

                } catch(FileNotFoundException e) {
                    PrefUtil.putBoolean(getActivity(), IAppConstants.BEFIT_CACHE_VALID_FLAG, false);
                } catch (IOException e) {
                    PrefUtil.putBoolean(getActivity(), IAppConstants.BEFIT_CACHE_VALID_FLAG, false);
                } catch (ApiException e) {
                    PrefUtil.putBoolean(getActivity(), IAppConstants.BEFIT_CACHE_VALID_FLAG, false);
                }
            }

            MatchesApi matchesApi = new MatchesApi();
            matchesApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            matchesApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            try {
                response = matchesApi.matchActivities(
                        50.0,
                        false,
                        false,
                        10.0);

                String toCacheString = ApiInvoker.serialize(response);
                File cacheFile = new File(getActivity().getCacheDir().getAbsolutePath() + "/" + GetEvents.class.getSimpleName());
                FileOutputStream toCacheStream = new FileOutputStream(cacheFile);
                toCacheStream.write(toCacheString.getBytes("UTF-8"));
                toCacheStream.close();
                PrefUtil.putBoolean(getActivity(), IAppConstants.BEFIT_CACHE_VALID_FLAG, true);
                PrefUtil.putLong(getActivity(), IAppConstants.BEFIT_CACHE_UPDATE_VALUE, System.currentTimeMillis());
            } catch (Exception e) {
                Log.e(this.getClass().getSimpleName(), "", e);
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (response != null)
                setEventList(response);
            if (progressDialog != null)
            {
                progressDialog.dismiss();
            }
        }
    }
}
