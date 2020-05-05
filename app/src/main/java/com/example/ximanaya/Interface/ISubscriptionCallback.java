package com.example.ximanaya.Interface;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback  {

    /**
     * 调用添加的时候，去通知UI结果
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除订阅的回调
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 获取订阅专辑加载的回调
     * @param albums
     */
    void onSubscriptionsLoaded(List<Album> albums);

    /**
     * 满了，提示
     */
    void onSubTooMany();
}
