package com.example.ximanaya.Predenter;

import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.Data.HistoryDao;
import com.example.ximanaya.Data.IHistoryDao;
import com.example.ximanaya.Data.IHistoryDaoCallback;
import com.example.ximanaya.Interface.IHistoryCallback;
import com.example.ximanaya.Interface.IHistoryPresenter;
import com.example.ximanaya.Utils.Constants;
import com.example.ximanaya.Utils.LogUtils;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 历史数量最多100条，超过就删除最早的，在吧新的加上
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private final IHistoryDao mHistoryDao;

    private List<IHistoryCallback> mIHistoryCallbacks=new ArrayList<>();
    private List<Track> mCurrenthistories=null;
    private boolean isDoDelAsOutOfSize =false;
    private Track mCurrentTrack=null;
    private static final String TAG = "HistoryPresenter";

    private HistoryPresenter(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }

    public static HistoryPresenter mHistoryPresenter=null;

    public static HistoryPresenter getHistoryPresenter(){
        if (mHistoryPresenter == null) {
            synchronized (HistoryPresenter.class){
                if (mHistoryPresenter == null) {
                    mHistoryPresenter=new HistoryPresenter();
                }
            }
        }
        return mHistoryPresenter;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void addHistory(final Track track) {
        //需要判断是否有>=100条
        if (mIHistoryCallbacks != null&&mIHistoryCallbacks.size()>= Constants.MAX_HISTORY_COUNT) {
            isDoDelAsOutOfSize =true;
            mCurrentTrack =track;
            //先不添加，线删除最后一个在添加
            delHistory(mCurrenthistories.get(mCurrenthistories.size()-1));
        }else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        //ui注册过过来
        if (!mIHistoryCallbacks.contains(iHistoryCallback)) {
            mIHistoryCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unregisterViewCallback(IHistoryCallback iHistoryCallback) {
        //删除ui的回调
        mIHistoryCallbacks.remove(iHistoryCallback);
    }



    @Override
    public void onHistoryAdd(boolean isSuccess) {
        //nothing to do.
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        //nothing to do.
        if (isDoDelAsOutOfSize &&mCurrentTrack!=null){
            isDoDelAsOutOfSize=false;
            //添加当前的数据进入数据库中
            addHistory(mCurrentTrack);
        }else {
            listHistories();
        }

    }

    @Override
    public void onHistoriesLoaded(final List<Track> tracks) {
        mCurrenthistories =tracks;
        LogUtils.d(TAG, "onHistoriesLoaded: histories size --> "+mCurrenthistories.size());
        //通知UI更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback iHistoryCallback : mIHistoryCallbacks) {
                    iHistoryCallback.onHistoriesLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        //nothing to do.
        listHistories();
    }
}
