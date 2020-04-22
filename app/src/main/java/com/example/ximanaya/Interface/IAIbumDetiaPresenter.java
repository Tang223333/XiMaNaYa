package com.example.ximanaya.Interface;

import com.example.ximanaya.Base.IBasePresenter;

public interface IAIbumDetiaPresenter extends IBasePresenter<IAlbumDetaViewCallback> {

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     */
    void getAIbumDetail(int albumId, int page);
}
