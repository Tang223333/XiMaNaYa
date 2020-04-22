package com.example.ximanaya.Interface;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetaViewCallback {

    /**
     * 专辑内容加载出来
     *
     * @param tracks
     */
    void onDetailListloaded(List<Track> tracks);

    /**
     * 传数据
     *
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 网络错误
     */
    void onNotworkError(int errorCod, String errorMag);
}
