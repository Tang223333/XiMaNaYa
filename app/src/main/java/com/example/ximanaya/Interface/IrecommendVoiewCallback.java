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
     * 网络错误
     */
    void onNetworkError();

    /**
     * 数据为空
     */
    void onEmpty();

    /**
     * 正在加载
     */
    void onLoading();
}
