package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fragments.FragmentInterest;
import fragments.FragmentNearYou;

/**
 * Created by jsfirme on 14-11-26.
 */
public class TabPagerAdapter extends FragmentPagerAdapter
{
    private String[] TABS;

    public TabPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    public TabPagerAdapter(FragmentManager fm, String[] tabs)
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
            fragment = new FragmentNearYou();
        }
        else if(arg0==1)
        {
            fragment = new FragmentInterest();
        }
        else if(arg0==2)
        {
            fragment = new FragmentInterest();
        }
        else if(arg0==3)
        {
            fragment = new FragmentNearYou();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        //number of tabs
        return TABS.length;
    }
}
