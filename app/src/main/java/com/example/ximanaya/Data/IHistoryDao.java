package com.example.ximanaya.Data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryDao {

    /**
     * 设置回调接口
     * @param callback
     */
    void setCallback(IHistoryDaoCallback callback);

    /**
     * 添加历史
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     * @param track
     */
    void delHistory(Track track);

    /**
     * 清除历史数据（all）
     */
    void clearHistory();

    /**
     * 获取历史数据
     */
    void listHistories();
}
