package com.example.ximanaya.Predenter;

import android.util.Log;

import com.example.ximanaya.Interface.IRecommendPresenter;
import com.example.ximanaya.Interface.IrecommendVoiewCallback;
import com.example.ximanaya.Utils.Constants;
import com.example.ximanaya.Utils.LogUtils;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {
    private RecommendPresenter(){}

    private List<IrecommendVoiewCallback> mCallbacks=new ArrayList<>();

    private static final String TAG = "RecommendPresenter";

    private static RecommendPresenter recommendPresenter=null;

    /**
     * 获取单例对象
     * @return
     */
    public static RecommendPresenter getInstance(){
        if (recommendPresenter==null){
            synchronized (RecommendPresenter.class){
                if (recommendPresenter==null){
                    recommendPresenter = new RecommendPresenter();
                }
            }
        }
        return recommendPresenter;
    }

    /**
     * 获取推荐内容，3.10.6 获取猜你喜欢专辑
     */
    @Override
    public void getRecommendList() {
        //获取数据
        //封装参数
        Map<String, String> map = new HashMap<String, String>();
        //这个参数表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //数据请求成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumLists = gussLikeAlbumList.getAlbumList();
                    Log.d(TAG, "onSuccess: "+albumLists.size());
                    handlerRecommendResult(albumLists);
                }
            }

            @Override
            public void onError(int i, String s) {
                //数据出错
                LogUtils.d(TAG, "errer --> " + i);
                LogUtils.d(TAG, "errerMsg --> " + s);
            }
        });
    }

    @Override
    public void pull2RefreshMore() {

    }


    private void handlerRecommendResult(List<Album> albumList) {
        //通知UI
        if (mCallbacks!=null){
            for(IrecommendVoiewCallback callback:mCallbacks){
                Log.d(TAG, "handlerRecommendResult: "+albumList.size());
                callback.onRecommendListLoaded(albumList);
            }
        }
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallBack(IrecommendVoiewCallback callback) {
        if (mCallbacks!=null&&!mCallbacks.contains(callback)){
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallBack(IrecommendVoiewCallback callback) {
        if (mCallbacks!=null){
            mCallbacks.remove(callback);
        }
    }
}
