package com.example.ximanaya.Interface;

import com.example.ximanaya.Base.IBasePresenter;

//实现接口，获取数据
public interface IRecommendPresenter extends IBasePresenter<IrecommendVoiewCallback> {
    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新更多内容
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();

}
