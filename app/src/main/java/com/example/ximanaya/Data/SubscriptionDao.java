package com.example.ximanaya.Data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ximanaya.Base.BaseApplication;
import com.example.ximanaya.Utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDao implements ISubDao {
    private static final SubscriptionDao ourInstance = new SubscriptionDao();
    private final XimalayaDbhelper mXimalayaDbhelper;
    private static final String TAG = "SubscriptionDao";
    private ISubDaoCallback mISubDaoCallback=null;

    public static SubscriptionDao getInstance() {
        return ourInstance;
    }

    private SubscriptionDao() {
        mXimalayaDbhelper = new XimalayaDbhelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        mISubDaoCallback =callback;
    }

    @Override
    public void addAlbum(Album album) {
        SQLiteDatabase db=null;
        boolean isAddSuccess=false;
        try {
            db = mXimalayaDbhelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues contentValues=new ContentValues();
            //封装数据
            contentValues.put(Constants.SUB_COVER_URL,album.getCoverUrlLarge());
            contentValues.put(Constants.SUB_TITLE,album.getAlbumTitle());
            contentValues.put(Constants.SUB_DESCRIPTION,album.getAlbumIntro());
            contentValues.put(Constants.SUB_TRACKS_COUNT,album.getIncludeTrackCount());
            contentValues.put(Constants.SUB_PLAY_COUNT,album.getPlayCount());
            contentValues.put(Constants.SUB_AUTHOR_NAME,album.getAnnouncer().getNickname());
            contentValues.put(Constants.SUB_ALBUM_ID,album.getId());
            //插入数据
            db.insert(Constants.SUB_TB_NAME,null,contentValues);
            db.setTransactionSuccessful();
            isAddSuccess=true;
        } catch (Exception e) {
            e.printStackTrace();
            isAddSuccess=false;
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mISubDaoCallback != null) {
                mISubDaoCallback.onAddResult(isAddSuccess);
            }
        }
    }

    @Override
    public void deleteAlbum(Album album) {
        SQLiteDatabase db=null;
        boolean isDeleteSuccess=false;
        try {
            db = mXimalayaDbhelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.SUB_TB_NAME, Constants.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            Log.d(TAG, "deleteAlbum: delete --> "+delete);
            db.setTransactionSuccessful();
            isDeleteSuccess=true;
        } catch (Exception e) {
            e.printStackTrace();
            isDeleteSuccess=false;
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mISubDaoCallback != null) {
                mISubDaoCallback.onDelResult(isDeleteSuccess);
            }
        }
    }

    @Override
    public void listAlbums() {
        SQLiteDatabase db=null;
        List<Album>  result=new ArrayList<>();
        try {
            db = mXimalayaDbhelper.getReadableDatabase();
            db.beginTransaction();
            Cursor query = db.query(Constants.SUB_TB_NAME, null, null, null, null, null, "_id desc");
            //封装数据
            while (query.moveToNext()) {
                Album album =new Album();
                //图片
                String coverUrl = query.getString(query.getColumnIndex(Constants.SUB_COVER_URL));
                album.setCoverUrlLarge(coverUrl);
                //标题
                String titel = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                album.setAlbumTitle(titel);
                //描述
                String description = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);
                //
                int tracks_count = query.getInt(query.getColumnIndex(Constants.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(tracks_count);
                //播放量
                int play_count = query.getInt(query.getColumnIndex(Constants.SUB_PLAY_COUNT));
                album.setPlayCount(play_count);
                //
                String author_name = query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME));
                Announcer announcer=new Announcer();
                announcer.setNickname(author_name);
                album.setAnnouncer(announcer);
                //专辑id
                int album_id = query.getInt(query.getColumnIndex(Constants.SUB_ALBUM_ID));
                album.setId(album_id);
                result.add(album);
            }
            query.close();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }

            //把数据通知出去
            if (mISubDaoCallback != null) {
                mISubDaoCallback.onSubListLoaded(result);
            }
        }
    }
}
