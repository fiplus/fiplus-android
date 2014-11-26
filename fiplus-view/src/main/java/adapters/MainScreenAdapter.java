package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fragments.FragmentInterest;
import fragments.FragmentNearYou;

/**
 * Created by jsfirme on 14-11-26.
 */
public class MainScreenAdapter extends FragmentPagerAdapter
{
    public MainScreenAdapter(FragmentManager fm)
    {
        super(fm);
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
        return fragment;
    }

    @Override
    public int getCount() {
        //number of fragments
        // TODO: There might be a way to grab number of added tabs
        return 2;
    }
}
