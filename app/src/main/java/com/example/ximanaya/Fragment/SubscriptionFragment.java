package com.example.ximanaya.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ximanaya.Base.BaseFragment;
import com.example.ximanaya.R;

public class SubscriptionFragment extends BaseFragment {
    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView =  layoutInflater.inflate(R.layout.fragment_subscription,container,false);
        return rootView;
    }
}
