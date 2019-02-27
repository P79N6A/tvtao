package com.yunos.tvtaobao.payment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * <pre>
 *     author : xutingting
 *     e-mail : xutingting@zhiping.tech
 *     time   : 2017/12/08
 *     desc   : 扫码和支付的ViewPagerAdapter
 *     version: 1.0
 * </pre>
 */
public class ViewPagerAdapter extends PagerAdapter {
    ArrayList<View> viewLists;

    public ViewPagerAdapter(ArrayList<View> lists)
    {
        viewLists = lists;
    }
    @Override
    public int getCount() {
        if(viewLists==null){
            return 0;
        }else {
        return viewLists.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(viewLists.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager)container).addView(viewLists.get(position));
        return viewLists.get(position);    }
}
