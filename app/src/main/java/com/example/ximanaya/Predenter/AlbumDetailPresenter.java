package com.example.ximanaya.Predenter;

import android.util.Log;

import com.example.ximanaya.Interface.IAIbumDetiaPresenter;
import com.example.ximanaya.Interface.IAlbumDetaViewCallback;
import com.example.ximanaya.Utils.Constants;
import com.example.ximanaya.Utils.LogUtils;
import com.example.ximanaya.api.XimalayaApi;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumDetailPresenter implements IAIbumDetiaPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetaViewCallback> callbacks=new ArrayList<>();
    private Album mTargetAlbum=null;
    //当前的专辑id
    private int mCurrentAlbumId=-1;
    //当前页
    private int mCurrentPageIndex=0;
    private List<Track>  mTracks=new ArrayList<>();

    private AlbumDetailPresenter() {
    }

    public static AlbumDetailPresenter sInstance=null;
    public static AlbumDetailPresenter getInstance(){
        if (sInstance==null){
            synchronized (AlbumDetailPresenter.class){
                if (sInstance==null){
                    sInstance=new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {
        //去加载更多类容
        mCurrentPageIndex++;
        //传入true表示结果会追加到列表后方
        doLoaded(true);
    }

    private void doLoaded(final boolean isLloaderMore){
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks=trackList.getTracks();
                    if (isLloaderMore) {
                        //上拉加载，结果放在后面去
                        mTracks.addAll(tracks);
                        int size = tracks.size();
                        handlerLoadermoreResult(size);
                    }else {
                        //下拉刷新，结果放到前面
                        mTracks.addAll(0,tracks);
                    }
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                if (isLloaderMore) {
                    mCurrentPageIndex--;
                }
                LogUtils.d(TAG, "onError: errprcpde -->" +i);
                LogUtils.d(TAG, "onError: errprMag -->" +s);
                handlerError(i,s);
            }
        },mCurrentAlbumId,mCurrentPageIndex);
    }

    //处理加载更多的结果
    private void handlerLoadermoreResult(int size) {
        for (IAlbumDetaViewCallback callback : callbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAIbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId=albumId;
        this.mCurrentPageIndex=page;
        doLoaded(false);
    }

    private void handlerError(int i, String s) {
        for (IAlbumDetaViewCallback callback:callbacks) {
            callback.onNotworkError(i,s);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetaViewCallback mCallback: callbacks) {
            mCallback.onDetailListloaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetaViewCallback callback) {
        if (callbacks!=null&&!callbacks.contains(callback)) {
            Log.d(TAG, "registerViewCallback: ");
            callbacks.add(callback);
            if (mTargetAlbum!=null){
                Log.d(TAG, "registerViewCallback: "+true);
                callback.onAlbumLoaded(mTargetAlbum);
            }else {
                Log.d(TAG, "registerViewCallback: "+false);
            }
        }
    }

    @Override
    public void unregisterViewCallback(IAlbumDetaViewCallback callback) {
        if (callbacks!=null&&!callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }

    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum=targetAlbum;
    }
}
