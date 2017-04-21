package com.esd.esd.biathlontimer;


import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class PagerAdapterHelper extends PagerAdapter
{
    List<View> pages;

    public PagerAdapterHelper(List<View> pages)
    {
        this.pages = pages;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        View v = pages.get(position);
        ((ViewPager)container).addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        ((ViewPager)container).removeView((View) object);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
