package com.Fiplus;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * Main screen
 */
public class MainScreenActivity extends FragmentActivity implements TabListener {

    private ViewPager viewPager;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstancesState)
    {
        super.onCreate(savedInstancesState);
        setContentView(R.layout.activity_mainscreen);

        viewPager=(ViewPager)findViewById(R.id.pager);
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
        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        actionBar=getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        setTabs();
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
    public void onTabReselected(Tab tab, FragmentTransaction ft)
    {

    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft)
    {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft)
    {

    }

}

class MyAdapter extends FragmentPagerAdapter
{
    public MyAdapter(FragmentManager fm)
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
