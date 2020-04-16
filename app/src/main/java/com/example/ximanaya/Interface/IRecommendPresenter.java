package com.example.ximanaya.Interface;

//实现接口，获取数据
public interface IRecommendPresenter {
    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉刷新
     */
    void pull2RefreshMore();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 这个方法用于注册UI的回调
     */
    void  registerViewCallBack(IrecommendVoiewCallback callback);

    /**
     * 取消UI的回调注册
     * @param callback
     */
    void unregisterViewCallBack(IrecommendVoiewCallback callback);
}
