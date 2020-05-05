package com.example.ximanaya.Data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryDaoCallback {

    /**
     * 添加历史回调接口
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史回调接口
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 加载历史数据
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);

    /**
     * 清除历史回调接口
     */
    void onHistoriesClean(boolean isSuccess);
}
