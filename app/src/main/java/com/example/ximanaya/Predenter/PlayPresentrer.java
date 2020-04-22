package com.example.ximanaya.Predenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.Interface.IPlayerPresenter;
import com.example.ximanaya.Interface.IPlayerViewCallBack;
import com.example.ximanaya.Utils.LogUtils;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayPresentrer implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private List<IPlayerViewCallBack> mIPlayerViewCallBacks=new ArrayList<>();

    private static PlayPresentrer sPlayPresentrer;
    private final XmPlayerManager mPlayerManager;
    private static final String TAG = "PlayPresentrer";
    private boolean isPlayListSet = false;
    private int mPlayIndex=-1;
    private Track mTrack;
    private boolean mIsReverse=false;
//    private final SharedPreferences mPlayMode;

    private PlayPresentrer() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //注册广告相关的接口
        mPlayerManager.addAdsStatusListener(this);
//        注册播放器状态相关的接口
        mPlayerManager.addPlayerStatusListener(this);
        //需要记录当前的播放模式
//        mPlayMode = BaseApplication.getAppContext().getSharedPreferences("PlayMode", Context.MODE_PRIVATE);

    }


    public static PlayPresentrer getPlayPresentrer() {
        if (sPlayPresentrer == null) {
            synchronized (PlayPresentrer.class) {
                if (sPlayPresentrer == null) {
                    sPlayPresentrer = new PlayPresentrer();
                }
            }
        }
        return sPlayPresentrer;
    }

    public void setPlayList(List<Track> list,int playIndex){
        LogUtils.d(TAG, "setPlayList: ");
        mPlayIndex=playIndex;
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list,mPlayIndex);
            isPlayListSet=true;
            mTrack = list.get(mPlayIndex);
        }else {
            LogUtils.d(TAG, "setPlayList: mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        if (isPlayListSet){
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void swichPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayMode(mode);
            //通知Ui更新播放模式
            for (IPlayerViewCallBack iPlayerViewCallBack : mIPlayerViewCallBacks) {
                iPlayerViewCallBack.onPlayModeChange(mode);
            }
//            SharedPreferences.Editor editor= mPlayMode.edit();
//            editor.putInt()
        }
    }

    //获取播放列表
    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerViewCallBack iPlayerViewCallBack : mIPlayerViewCallBacks) {
                iPlayerViewCallBack.onListLoading(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到底index的位置进行播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTe(int progress) {
        //更新播放器的进度
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlay() {
        //返回当前是否正在播放
        return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {
        //把播放列表反转
        List<Track> playList=mPlayerManager.getPlayList();
        Collections.reverse(playList);
        mIsReverse=!mIsReverse;
        //第一个参数是播放列表，第二个参数是开始播放的下标
        mPlayIndex=playList.size()-1-mPlayIndex;
        mPlayerManager.setPlayList(playList,mPlayIndex);
        //更新UI
        mTrack= (Track) mPlayerManager.getCurrSound();
        for (IPlayerViewCallBack iPlayerViewCallBack : mIPlayerViewCallBacks) {
            iPlayerViewCallBack.onListLoading(playList);
            iPlayerViewCallBack.onTrackUpdate(mTrack,mPlayIndex);
            iPlayerViewCallBack.onUpdateListOrder(mIsReverse);
        }
    }

    @Override
    public void registerViewCallback(IPlayerViewCallBack iPlayerViewCallBack) {
        if (mIPlayerViewCallBacks!=null&&!mIPlayerViewCallBacks.contains(iPlayerViewCallBack)){
            mIPlayerViewCallBacks.add(iPlayerViewCallBack);
        }
        LogUtils.d(TAG, "registerViewCallback: "+mIPlayerViewCallBacks.size());
        for (IPlayerViewCallBack playerViewCallBack : mIPlayerViewCallBacks) {
            playerViewCallBack.onTrackUpdate(mTrack,mPlayIndex);
        }
        if (mPlayerManager != null) {
            XmPlayListControl.PlayMode playMode=mPlayerManager.getPlayMode();
            for (IPlayerViewCallBack playerViewCallBack : mIPlayerViewCallBacks) {
                playerViewCallBack.onPlayModeChange(playMode);
            }
        }

    }

    @Override
    public void unregisterViewCallback(IPlayerViewCallBack iPlayerViewCallBack) {
        if (mIPlayerViewCallBacks!=null&&!mIPlayerViewCallBacks.contains(iPlayerViewCallBack)){
            mIPlayerViewCallBacks.remove(iPlayerViewCallBack);
        }
    }

    //广告相关的回调方法,start
    @Override
    public void onStartGetAdsInfo() {
        LogUtils.d(TAG, "onStartGetAdsInfo: onStartGetAdsInfo..");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtils.d(TAG, "onGetAdsInfo: onGetAdsInfo..");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtils.d(TAG, "onAdsStartBuffering: onAdsStartBuffering..");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtils.d(TAG, "onAdsStopBuffering: onAdsStopBuffering..");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtils.d(TAG, "onStartPlayAds: onStartPlayAds..");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtils.d(TAG, "onCompletePlayAds: onCompletePlayAds..");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtils.d(TAG, "onError: what -> "+what+" extra -> "+extra);
    }

    //广告相关的回调方法,end

    //播放器相关的回调方法,start
    @Override
    public void onPlayStart() {
        LogUtils.d(TAG, "onPlayStart: ");
        for (IPlayerViewCallBack iPlayerViewCallBack:mIPlayerViewCallBacks) {
            if (mPlayerManager != null) {
                iPlayerViewCallBack.onPlayStart();
            }
        }
    }

    @Override
    public void onPlayPause() {
        LogUtils.d(TAG, "onPlayPause: ");
        for (IPlayerViewCallBack iPlayerViewCallBack:mIPlayerViewCallBacks) {
            iPlayerViewCallBack.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtils.d(TAG, "onPlayStop: ");
        for (IPlayerViewCallBack iPlayerViewCallBack:mIPlayerViewCallBacks) {
            iPlayerViewCallBack.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtils.d(TAG, "onSoundPlayComplete: ");
    }

    @Override
    public void onSoundPrepared() {
        LogUtils.d(TAG, "onSoundPrepared: ");
        if (mPlayerManager.getPlayerStatus()== PlayerConstants.STATE_PREPARED) {
            //播放器准备完成，开始播放
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        if (lastModel!=null){
            LogUtils.d(TAG, "onSoundSwitch: PlayableModel -> "+lastModel.getKind());
        }
        if (curModel!=null){
            LogUtils.d(TAG, "onSoundSwitch: "+" PlayableModel -> "+curModel.getKind());
        }
        //curModel代表是当前播放的内容
        //通过getKind()方法来获取他的类型
        //第一种写法（不推荐）
        //if ("track".equals(curModel.getKind())) {
        //    Track currentTrack = (Track) curModel;
        //    LogUtils.d(TAG, "onSoundSwitch: "+currentTrack.getTrackTitle());
        //}

        mPlayIndex=mPlayerManager.getCurrentIndex();

        if (curModel instanceof Track) {
            mTrack = (Track) curModel;
            LogUtils.d(TAG, "onSoundSwitch: "+mIPlayerViewCallBacks.size());
            for (IPlayerViewCallBack iPlayerViewCallBack : mIPlayerViewCallBacks) {
                Log.d(TAG, "onSoundSwitch: "+mTrack.getTrackTitle());
                iPlayerViewCallBack.onTrackUpdate(mTrack,mPlayIndex);
            }
        }

    }

    @Override
    public void onBufferingStart() {
        LogUtils.d(TAG, "onBufferingStart: ");
    }

    @Override
    public void onBufferingStop() {
        LogUtils.d(TAG, "onBufferingStop: ");
    }

    @Override
    public void onBufferProgress(int progress) {
        LogUtils.d(TAG, "onBufferProgress: progress -> "+progress);
    }

    @Override
    public void onPlayProgress(int current, int duration) {
        //单位是毫秒
        for (IPlayerViewCallBack iPlayerViewCallBack : mIPlayerViewCallBacks) {
            iPlayerViewCallBack.onProgressChange(current,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtils.d(TAG, "XmPlayerException: XmPlayerException -> "+e);
        return false;
    }
    //播放器相关的回调方法,end


}
