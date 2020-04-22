package com.example.ximanaya.Interface;

import android.os.Trace;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerViewCallBack {

    //开始播放
    void onPlayStart();

    //播放暂停
    void onPlayPause();

    //播放停止
    void onPlayStop();

    //播放错误
    void onPlayError(int i);

    //下一首
    void onNextPlay(Track Track);

    //上一首
    void onProPlay(Track Track);

    //播放列表返回数据
    //list 播放列表数据
    void onListLoading(List<Track> list);

    //播放模式改变
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    //进度改变
    void onProgressChange(int currentProgress,int total);

    //广告正在加载
    void onAdLoading();

    //广告结束
    void onAdFubusged();

    //更新当前节目
    void onTrackUpdate(Track track,int playIndex);

    //通知UI更新播放列表顺序或逆序图标
    void onUpdateListOrder(boolean isReverse);
}
