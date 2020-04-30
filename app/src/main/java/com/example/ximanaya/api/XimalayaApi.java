package com.example.ximanaya.api;

import com.example.ximanaya.Utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class XimalayaApi {

    public XimalayaApi(){}

    public static XimalayaApi sXimalayaApi;

    public static XimalayaApi getXimalayaApi(){
        if (sXimalayaApi == null) {
            synchronized (XimalayaApi.class){
                if (sXimalayaApi==null) {
                    sXimalayaApi=new XimalayaApi();
                }
            }
        }
        return sXimalayaApi;
    }

    /**
     * 获取推荐类容
     * @param callBack 回调结果的回调接口
     */
    public void getRecommondList(IDataCallBack<GussLikeAlbumList> callBack){
        Map<String, String> map = new HashMap<String, String>();
        //这个参数表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map,callBack);
    }

    /**
     * 根据专辑ID获取到详情内容
     * @param callBack 回调结果的回调接口
     * @param albumId 专辑的id
     * @param pageIndex 页数
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack,long albumId,int pageIndex){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DETAIL+"");
        CommonRequest.getTracks(map,callBack);
    }

    /**
     * 根据关键词搜索
     * @param keyword
     */
    public void searchByKeyword(String keyword,int page,IDataCallBack<SearchAlbumList> callBack) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE,Constants.RECOMMEND_COUNT+"");
        CommonRequest.getSearchedAlbums(map,callBack);
    }

    /**
     * 获取推荐的热词
     * @param callBack
     */
    public void getHotWords(IDataCallBack<HotWordList> callBack){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.TOP, Constants.COUNT_HOT_WORD+"");
        CommonRequest.getHotWords(map, callBack);
    }

    /**
     * 根据关键字获取联想词
     * @param keyword 关键字
     * @param callBack 回调方法
     */
    public void getSuggestWord(String keyword, IDataCallBack<SuggestWords> callBack){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map, callBack);
    }
}
