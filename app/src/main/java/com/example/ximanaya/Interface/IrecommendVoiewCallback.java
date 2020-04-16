package com.example.ximanaya.Interface;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

//实现接口，更新UI
public interface IrecommendVoiewCallback {

    /**
     * 获取推荐内容的结果
     * @param result
     */
    void onRecommendListLoaded(List<Album> result);

    /**
     * 加载更多
     * @param result
     */
    void onLoaderMore(List<Album> result);

    /**
     * 下拉刷新
     * @param result
     */
    void onRefreshMore(List<Album> result);
}
