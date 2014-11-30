package fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Fiplus.R;
import com.astuetz.PagerSlidingTabStrip;

import adapters.RecentTabPagerAdapter;

public class FragmentRecentActivities extends Fragment {

    public static final String TAG = FragmentRecentActivities.class
            .getSimpleName();

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent_activities, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.recent_activities_tabs);
        ViewPager pager = (ViewPager) view.findViewById(R.id.recent_activities_pager);


        RecentTabPagerAdapter adapter =
                new RecentTabPagerAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.recent_activities_tab_items));

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }

}
