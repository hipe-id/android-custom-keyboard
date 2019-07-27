package id.hipe.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cak_upik on 4/26/18.
 */

public class CustomViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragmentList;
    private List<String> mTitleList;

    public CustomViewPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentList = new ArrayList<>();
        mTitleList = new ArrayList<>();
    }

    public void addPage(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mTitleList.add(title);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitleList.get(position);
    }
}
