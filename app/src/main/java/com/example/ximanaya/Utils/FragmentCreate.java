package com.example.ximanaya.Utils;

import com.example.ximanaya.Base.BaseFragment;
import com.example.ximanaya.Fragment.HistoryFragment;
import com.example.ximanaya.Fragment.RecommendFragment;
import com.example.ximanaya.Fragment.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreate {

    public final static int INDEX_RECOMMEND=0;
    public final static int INDEX_SUBSCRIPTION=1;
    public final static int INDEX_HISTORY=2;

    public final static int PACE_COUNT=3;

    private static Map<Integer, BaseFragment> sCache=new HashMap<>();

    public static BaseFragment getFragement(int index){
        BaseFragment baseFragment=sCache.get(index);
        if (baseFragment!=null){
            return baseFragment;
        }
        switch (index){
            case INDEX_RECOMMEND:
                baseFragment=new RecommendFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment=new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment=new HistoryFragment();
                break;
        }

        sCache.put(index,baseFragment);
        return baseFragment;
    }
}
