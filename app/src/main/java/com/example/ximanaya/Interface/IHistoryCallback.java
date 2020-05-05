package com.example.ximanaya.Interface;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryCallback {

    /**
     * 历史类容加载
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);
}
