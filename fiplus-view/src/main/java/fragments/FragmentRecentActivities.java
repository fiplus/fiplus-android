package fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.Fiplus.FiplusApplication;
import com.Fiplus.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.wordnik.client.ApiException;
import com.wordnik.client.ApiInvoker;
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

public class FragmentRecentActivities extends Fragment {

    public static final String TAG = FragmentRecentActivities.class
            .getSimpleName();

    private ListView mEventsList;
    private EventListAdapter mEventListAdapter;
    private SwipeRefreshLayout mSwipeLayout;

    public static FragmentRecentActivities newInstance() {
        return new FragmentRecentActivities();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Tracker t = ((FiplusApplication)getActivity().getApplication()).getTracker();
        t.setScreenName(this.getClass().getSimpleName());
        t.send(new HitBuilders.ScreenViewBuilder().build());

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_generic_list, container, false);
        mEventsList = (ListView) v.findViewById(R.id.eventsList);
        //mEventsList.setOnItemClickListener(new EventItemClickListener());

        mSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeLayout.setRefreshing(true);
                GetRecentEvents getRecentEvents = new GetRecentEvents();
                getRecentEvents.execute();
            }
        });
        GetRecentEvents getRecentEvents= new GetRecentEvents();
        getRecentEvents.execute();
        return v;
    }

    private void setEventList(List<Activity> activities)
    {
        if (activities == null)
        {
            return;
        }

        ArrayList<EventListItem> eventList = new ArrayList<EventListItem>();

        for(int i = 0; i < activities.size(); i++)
            eventList.add(new EventListItem(
                    R.drawable.ic_configure,
                    activities.get(i).getName(),
                    LocationUtil.getLocationStrings(activities.get(i).getLocations(), getActivity().getBaseContext()),
                    activities.get(i).getTimes(),
                    ((Integer)activities.get(i).getMax_attendees().intValue()).toString(),
                    activities.get(i).getActivity_id()));

        mEventListAdapter = new EventListAdapter(getActivity(), eventList, TAG);
        mEventsList.setAdapter(mEventListAdapter);
        mEventListAdapter.notifyDataSetChanged();

    }

//    protected class EventItemClickListener implements AdapterView.OnItemClickListener {
//    @Override
//     public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//        {
//            String sEventID = mEventListAdapter.getItem(position).getEventId();
//            Intent intent = new Intent(getActivity(), ViewEventActivity.class);
//            intent.putExtra("eventID", sEventID);
//            startActivity(intent);
//        }
//    }

    private class GetRecentEvents extends AsyncTask<Void, Void, String>
    {
        protected List<Activity> response;
        protected ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            if(!mSwipeLayout.isRefreshing())
                progressDialog= ProgressDialog.show(getActivity(), getString(R.string.view_event_progress_bar_title) + "s...", getString(R.string.progress_dialog_text), true);
        }

        @Override
        protected String doInBackground(Void... params)
        {
            UsersApi usersApi = new UsersApi();
            usersApi.addHeader("X-DreamFactory-Application-Name", IAppConstants.APP_NAME);
            usersApi.setBasePath(IAppConstants.DSP_URL + IAppConstants.DSP_URL_SUFIX);

            if(PrefUtil.getBoolean(getActivity(), IAppConstants.RECENT_EVENTS_CACHE_VALID_FLAG, false)
                    && (System.currentTimeMillis() - PrefUtil.getLong(getActivity(),IAppConstants.RECENT_EVENTS_CACHE_UPDATE_VALUE)) < IAppConstants.RECENT_EVENTS_CACHE_VALID_TIME)
            {
                try
                {
                    File cacheFile = new File(getActivity().getCacheDir() + "/" + GetRecentEvents.class.getSimpleName());
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
                    response = (List<Activity>) ApiInvoker.deserialize(cachedJson, "List", Activity.class);
                    cacheBytes.close();
                    cacheIn.close();
                    return null;

                } catch(FileNotFoundException e) {
                    PrefUtil.putBoolean(getActivity(), IAppConstants.RECENT_EVENTS_CACHE_VALID_FLAG, false);
                } catch (IOException e) {
                    PrefUtil.putBoolean(getActivity(), IAppConstants.RECENT_EVENTS_CACHE_VALID_FLAG, false);
                } catch (ApiException e) {
                    PrefUtil.putBoolean(getActivity(), IAppConstants.RECENT_EVENTS_CACHE_VALID_FLAG, false);
                }
            }

            try{
                response = usersApi.getActivities(PrefUtil.getString(getActivity().getBaseContext(), IAppConstants.USER_ID),
                                                  true,false);

                String toCacheString = ApiInvoker.serialize(response);
                File cacheFile = new File(getActivity().getCacheDir().getAbsolutePath() + "/" + GetRecentEvents.class.getSimpleName());
                FileOutputStream toCacheStream = new FileOutputStream(cacheFile);
                toCacheStream.write(toCacheString.getBytes("UTF-8"));
                toCacheStream.close();
                PrefUtil.putBoolean(getActivity(), IAppConstants.RECENT_EVENTS_CACHE_VALID_FLAG, true);
                PrefUtil.putLong(getActivity(), IAppConstants.RECENT_EVENTS_CACHE_UPDATE_VALUE, System.currentTimeMillis());
            } catch (Exception e) {
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (response != null)
                setEventList(response);
            if(progressDialog != null)
                progressDialog.dismiss();
            mSwipeLayout.setRefreshing(false);
        }
    }
}
