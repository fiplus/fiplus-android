package fragments;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Fiplus.R;

import adapters.MainScreenAdapter;

public class FragmentWhatsHappening extends Fragment implements ActionBar.TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;
    public static final String TAG = FragmentWhatsHappening.class.getSimpleName();

    public static FragmentWhatsHappening newInstance()
    {
        return new FragmentWhatsHappening();
    }

    @Override
    //protected void onCreate(Bundle savedInstancesState)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //super.onCreate(savedInstancesState);
        //setContentView(R.layout.activity_mainscreen);
        View v = inflater.inflate(R.layout.fragment_whats_happening, container, false);

        /**
         * Fragments
         */
        viewPager=(ViewPager)v.findViewById(R.id.pager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int arg0, float v, int i2) {

            }

            @Override
            public void onPageSelected(int arg0) {
                actionBar.setSelectedNavigationItem(arg0);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        viewPager.setAdapter(new MainScreenAdapter(getFragmentManager()));
        actionBar=getActivity().getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        setTabs();

        return v;
    }
    private void setTabs()
    {
        ActionBar.Tab tabNearYou=actionBar.newTab();
        tabNearYou.setText(getString(R.string.main_near));
        tabNearYou.setTabListener(this);

        ActionBar.Tab tabFavorite=actionBar.newTab();
        tabFavorite.setText(getString(R.string.main_favorite));
        tabFavorite.setTabListener(this);

        actionBar.addTab(tabNearYou);
        actionBar.addTab(tabFavorite);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
    {

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
    {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
    {

    }

}
