package com.example.ximanaya.Predenter;

import android.util.Log;

import com.example.ximanaya.Interface.IAIbumDetiaPresenter;
import com.example.ximanaya.Interface.IAlbumDetaViewCallback;
import com.example.ximanaya.Utils.Constants;
import com.example.ximanaya.Utils.LogUtils;
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

    }

    @Override
    public void getAIbumDetail(int albumId, int page) {
        //根据页面和专辑id获取
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DETAIL+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                LogUtils.d(TAG, "onSuccess: "+Thread.currentThread().getName());
                if (trackList != null) {
                    List<Track> tracks=trackList.getTracks();
                    Log.d(TAG, "onSuccess: "+tracks.size());
                    handlerAlbumDetailResult(tracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.d(TAG, "onError: errprcpde -->" +i);
                Log.d(TAG, "onError: errprMag -->" +s);
                handlerError(i,s);
            }
        });
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
