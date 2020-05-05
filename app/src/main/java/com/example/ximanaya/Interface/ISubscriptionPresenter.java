package com.example.ximanaya.Interface;

import com.example.ximanaya.Base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * 订阅一般有上限,如不能超过100
 */
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback> {

    /**
     * 添加订阅
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅列表
     */
    void getSubscription();

    /**
     * 当前专辑是否已收藏
     * @param album
     */
    boolean isSub(Album album);
}
