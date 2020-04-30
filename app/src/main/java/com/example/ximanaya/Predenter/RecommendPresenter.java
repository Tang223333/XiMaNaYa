package com.example.ximanaya.Predenter;

import android.util.Log;

import com.example.ximanaya.Interface.IRecommendPresenter;
import com.example.ximanaya.Interface.IrecommendVoiewCallback;
import com.example.ximanaya.Utils.LogUtils;
import com.example.ximanaya.api.XimalayaApi;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

public class RecommendPresenter implements IRecommendPresenter {

    private List<Album> mCurrentRecommend = null;

    private RecommendPresenter() {
    }

    private List<IrecommendVoiewCallback> mCallbacks = new ArrayList<>();

    private static final String TAG = "RecommendPresenter";

    private static RecommendPresenter recommendPresenter = null;

    /**
     * 获取单例对象
     *
     * @return
     */
    public static RecommendPresenter getInstance() {
        if (recommendPresenter == null) {
            synchronized (RecommendPresenter.class) {
                if (recommendPresenter == null) {
                    recommendPresenter = new RecommendPresenter();
                }
            }
        }
        return recommendPresenter;
    }

    /**
     * 获取当前的推荐专辑列表
     * @return 使用之前要盘空哦
     */
    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }

    /**
     * 获取推荐内容，3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //获取数据
        //封装参数
        updateLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommondList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //数据请求成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumLists = gussLikeAlbumList.getAlbumList();
                    Log.d(TAG, "onSuccess: " + albumLists.size());
                    handlerRecommendResult(albumLists);
                }
            }

            @Override
            public void onError(int i, String s) {
                //数据出错
                LogUtils.d(TAG, "errer --> " + i);
                LogUtils.d(TAG, "errerMsg --> " + s);
                handlerErrer();
            }
        });
    }

    @Override
    public void pull2RefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    private void handlerErrer() {
        //通知UI
        if (mCallbacks != null) {
            for (IrecommendVoiewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }


    private void handlerRecommendResult(List<Album> albumList) {
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IrecommendVoiewCallback callback : mCallbacks) {
                    LogUtils.d(TAG, "handlerRecommendResult: " + albumList.size());
                    callback.onEmpty();
                }
            } else {
                for (IrecommendVoiewCallback callback : mCallbacks) {
                    LogUtils.d(TAG, "handlerRecommendResult: " + albumList.size());
                    callback.onRecommendListLoaded(albumList);
                }
                mCurrentRecommend = albumList;
            }
        }
    }

    private void updateLoading() {
        for (IrecommendVoiewCallback callback : mCallbacks) {
            callback.onLoading();
        }
    }

    @Override
    public void registerViewCallback(IrecommendVoiewCallback callback) {
        Log.d(TAG, "registerViewCallBack: " + mCallbacks.contains(callback) + "  " + mCallbacks.size());
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(IrecommendVoiewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
