package fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Fiplus.FiplusApplication;
import com.Fiplus.R;
import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import adapters.TabPagerAdapter;

/**
 * Created by jsfirme on 14-11-26.
 */
public class FragmentWhatsHappening extends Fragment {

    public static final String TAG = FragmentWhatsHappening.class
            .getSimpleName();

    public static FragmentWhatsHappening newInstance() {
        return new FragmentWhatsHappening();
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
        return inflater.inflate(R.layout.fragment_whats_happening, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);


        TabPagerAdapter adapter =
                new TabPagerAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.tab_items));

        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
    }

}