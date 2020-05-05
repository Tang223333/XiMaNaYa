package com.example.ximanaya.Predenter;

import android.util.Log;

import com.example.ximanaya.Interface.ISearchCallback;
import com.example.ximanaya.Interface.ISearchPresenter;
import com.example.ximanaya.Utils.Constants;
import com.example.ximanaya.Utils.LogUtils;
import com.example.ximanaya.Data.XimalayaApi;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {

    private List<Album> searchResult=new ArrayList<>();

    private static final String TAG = "SearchPresenter";
    //当前的搜索关键字
    private String mCurrentKeyword=null;
    private XimalayaApi mXimalayaApi;
    private static final int DEFAULT_PAGE=1;
    private int mCurrentPage=DEFAULT_PAGE;

    private SearchPresenter(){
        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }

    private static SearchPresenter mSearchPresenter=null;

    public static SearchPresenter getSearchPresenter() {
        if (mSearchPresenter==null){
            synchronized (SearchPresenter.class){
                if (mSearchPresenter==null) {
                    mSearchPresenter=new SearchPresenter();
                }
            }
        }
        return mSearchPresenter;
    }

    private List<ISearchCallback> mCallback=new ArrayList<>();

    @Override
    public void doSearch(String keyword) {
        mCurrentPage = DEFAULT_PAGE;
        searchResult.clear();
        //用于重新搜索
        this.mCurrentKeyword=keyword;
        search(keyword);
    }

    private void search(String keyword) {
        mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                searchResult.addAll(albums);
                if (mIsLoadMore) {
                    for (ISearchCallback iSearchCallback : mCallback) {
                        iSearchCallback.onLoadmoreResult(searchResult,albums.size()!=0);
                    }
                    mIsLoadMore=false;
                }else {
                    for (ISearchCallback iSearchCallback : mCallback) {
                        iSearchCallback.onSearchResultLoaded(searchResult);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "onError: errorCode -->"+errorCode);
                LogUtils.d(TAG, "onError: errorMsg -->"+errorMsg);
                for (ISearchCallback iSearchCallback : mCallback) {
                    if (mIsLoadMore) {
                        iSearchCallback.onLoadmoreResult(searchResult,false);
                        mIsLoadMore=false;
                        mCurrentPage--;
                    }else {
                        iSearchCallback.onError(errorCode,errorMsg);
                    }

                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    private boolean mIsLoadMore=false;

    @Override
    public void loadMore() {
        //判断有没有必要加载更多
//        int size=searchResult.size()% Constants.COUNT_DETAIL;
//        if (size!=0) {
        if (searchResult.size()<Constants.COUNT_DETAIL){
            for (ISearchCallback iSearchCallback : mCallback) {
                iSearchCallback.onLoadmoreResult(searchResult,false);
            }
        }else {
            mIsLoadMore=true;
            mCurrentPage++;
            search(mCurrentKeyword);
        }
    }

    @Override
    public void getHotWord() {
        //todo:热词缓存
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    if (hotWordList != null) {
                        LogUtils.d(TAG, "onSuccess: hotWords size --> "+hotWords.size());
                    }else {
                        LogUtils.d(TAG, "onSuccess: hotWords is null");
                    }
                    if (mCallback != null) {
                        Log.d(TAG, "onSuccess: mCallback size --> "+mCallback.size());
                        for (ISearchCallback iSearchCallback : mCallback) {
                            iSearchCallback.onHotWordLoaded(hotWords);
                        }
                    }else {
                        Log.d(TAG, "onSuccess: mCallback is null");
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "onError: errorCode -->"+errorCode);
                LogUtils.d(TAG, "onError: errorMsg -->"+errorMsg);
            }
        });
    }

    @Override
    public void getRecommendMord(final String keyword) {
        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    if (keyword != null) {
                        Log.d(TAG, "onSuccess: keyWordList size --> "+keyWordList.size());
                    }else {
                        Log.d(TAG, "onSuccess: keyWordList is null");
                    }
                    if (mCallback != null) {
                        for (ISearchCallback iSearchCallback : mCallback) {
                            iSearchCallback.onRecommendWordLoaded(keyWordList);
                        }
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtils.d(TAG, "onError: errorCode -->"+errorCode);
                LogUtils.d(TAG, "onError: errorMsg -->"+errorMsg);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (mCallback!=null&&!mCallback.contains(iSearchCallback)) {
            mCallback.add(iSearchCallback);
        }
    }

    @Override
    public void unregisterViewCallback(ISearchCallback iSearchCallback) {
        mCallback.remove(iSearchCallback);
    }
}
