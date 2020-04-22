package com.example.ximanaya.Base;

public interface IBasePresenter<T> {

    /**
     * 注册UI通知接口
     */
    void registerViewCallback(T t);

    /**
     * 删除注册UI通知接口
     */
    void unregisterViewCallback(T m);
}
