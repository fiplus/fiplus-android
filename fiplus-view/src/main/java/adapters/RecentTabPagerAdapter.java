package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fragments.FragmentEvents;
import fragments.FragmentPeople;
import fragments.FragmentRecentActivities;

/**
 * Created by Nick on 11/30/2014.
 */
public class RecentTabPagerAdapter extends FragmentPagerAdapter
{
    private String[] TABS;

    public RecentTabPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    public RecentTabPagerAdapter(FragmentManager fm, String[] tabs)
    {
        super(fm);
        TABS = tabs;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return TABS[position];
    }

    @Override
    public Fragment getItem(int arg0)
    {
        Fragment fragment=null;
        if(arg0==0)
        {
            fragment = new FragmentEvents();
        }
        else if(arg0==1)
        {
            fragment = new FragmentPeople();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        //number of tabs
        return TABS.length;
    }
}
