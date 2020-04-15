package com.example.ximanaya.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.ximanaya.Utils.FragmentCreate;

public class MainContentAdapter extends FragmentPagerAdapter {
    public MainContentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentCreate.getFragement(position);
    }

    @Override
    public int getCount() {
        return FragmentCreate.PACE_COUNT;
    }
}
