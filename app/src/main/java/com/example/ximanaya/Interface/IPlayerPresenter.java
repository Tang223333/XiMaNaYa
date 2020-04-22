package com.example.ximanaya.Interface;

import com.example.ximanaya.Base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

public interface IPlayerPresenter extends IBasePresenter<IPlayerViewCallBack> {


    //播放
    void play();

    //暂停
    void pause();

    //停止播放
    void stop();

    //上一首
    void playPre();

    //下一首
    void playNext();

    //切换播放模式
    void swichPlayMode(XmPlayListControl.PlayMode mode);

    //获取播放列表
    void getPlayList();

    //根据节目的位置进行播放
    //index 节目在列表中的位置
    void playByIndex(int index);

    //切换播放进度
    void seekTe(int progress);

    /**
     * 判断播放器是否正在播放
     * @return
     */
    boolean isPlay();

    //把播放器列表内容翻转
    void reversePlayList();

}
