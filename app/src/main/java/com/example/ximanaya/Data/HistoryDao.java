package com.example.ximanaya.Data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.Utils.Constants;
import com.example.ximanaya.Utils.LogUtils;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryDao implements IHistoryDao{

    private final XimalayaDbhelper mDbhelper;
    private IHistoryDaoCallback mCallback=null;
    private static final String TAG = "HistoryDao";
    private Object mLock=new Object();

    public HistoryDao(){
        mDbhelper = new XimalayaDbhelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        mCallback =callback;
    }

    @Override
    public void addHistory(Track track) {
        synchronized (mLock){
            SQLiteDatabase db = null;
            boolean isSuccess=false;
            try {
                db = mDbhelper.getWritableDatabase();
                db.beginTransaction();
                //先去删除
                int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
                LogUtils.d(TAG, "addHistory: delete --> "+delete);
                //删除后在添加
                ContentValues values=new ContentValues();
                //封装数据
                values.put(Constants.HISTORY_TITLE ,track.getTrackTitle());
                values.put(Constants.HISTORY_PLAY_COUNT ,track.getPlayCount());
                values.put(Constants.HISTORY_TRACK_ID ,track.getDataId());
                values.put(Constants.HISTORY_DURATION ,track.getDuration());
                values.put(Constants.HISTORY_UPDATE_TIME ,track.getUpdatedAt());
                values.put(Constants.HISTORY_COVER ,track.getCoverUrlLarge());
                values.put(Constants.HISTORY_AUTHOR ,track.getAnnouncer().getNickname());
                //插入数据
                db.insert(Constants.HISTORY_TB_NAME,null,values);
                db.setTransactionSuccessful();
                isSuccess=true;
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess=false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }

                if (mCallback != null) {
                    mCallback.onHistoryAdd(isSuccess);
                }
            }
        }
    }

    @Override
    public void delHistory(Track track) {
        synchronized (mLock){
            SQLiteDatabase db = null;
            boolean isSuccess=false;
            try {
                db = mDbhelper.getWritableDatabase();
                db.beginTransaction();
                int delete= db.delete(Constants.HISTORY_TB_NAME,Constants.HISTORY_TRACK_ID+"=?",new String[]{track.getDataId()+""});
                LogUtils.d(TAG, "delHistory: "+delete);
                db.setTransactionSuccessful();
                isSuccess=true;
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess=false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }

                if (mCallback != null) {
                    mCallback.onHistoryDel(isSuccess);
                }
            }
        }
    }

    @Override
    public void clearHistory() {
        synchronized (mLock){
            SQLiteDatabase db = null;
            boolean isSuccess=false;
            try {
                db = mDbhelper.getWritableDatabase();
                db.beginTransaction();
                int delete = db.delete(Constants.HISTORY_TB_NAME, null, null);
                LogUtils.d(TAG, "clearHistory: "+delete);
                db.setTransactionSuccessful();
                isSuccess=true;
            } catch (Exception e) {
                e.printStackTrace();
                isSuccess=false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }

                if (mCallback != null) {
                    mCallback.onHistoriesClean(isSuccess);
                }
            }
        }
    }

    @Override
    public void listHistories() {
        synchronized (mLock){
            //从数据表中查出所有历史记录
            SQLiteDatabase db=null;
            List<Track> histories=new ArrayList<>();
            try {
                db = mDbhelper.getReadableDatabase();
                db.beginTransaction();
                Cursor cursor = db.query(Constants.HISTORY_TB_NAME, null, null, null, null, null, "_id desc");
                while (cursor.moveToNext()) {
                    Track track=new Track();
                    String title = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    int playCount = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    int trackId = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    int duration = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_DURATION));
                    track.setDuration(duration);
                    long updateTime = cursor.getLong(cursor.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    String cover = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    track.setCoverUrlMiddle(cover);
                    track.setCoverUrlSmall(cover);
                    String author=cursor.getString(cursor.getColumnIndex(Constants.HISTORY_AUTHOR));
                    Announcer announcer=new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);
                    histories.add(track);
                }
                Log.d(TAG, "listHistories: "+histories.size());
                cursor.close();
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }

                Log.d(TAG, "listHistories: "+histories.size());

                //通知出去
                if (mCallback != null) {
                    mCallback.onHistoriesLoaded(histories);
                }
            }
        }
    }
}
