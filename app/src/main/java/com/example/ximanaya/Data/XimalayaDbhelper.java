package com.example.ximanaya.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.ximanaya.Utils.Constants;

public class XimalayaDbhelper extends SQLiteOpenHelper {

    private static final String TAG = "XimalayaDbhelper";

    public XimalayaDbhelper(@Nullable Context context) {
        //name 数据库的名字，Factory游标工厂，version版本号
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: ...");
        //创建数据表
        //订阅相关的字段
        String subTbSql = "CREATE TABLE " + Constants.SUB_TB_NAME + " ( "+Constants.SUB_ID+" INTEGER primary key autoincrement, "+Constants.SUB_COVER_URL+" VARCHAR, "+Constants.SUB_TITLE+" VARCHAR, "+Constants.SUB_DESCRIPTION+" VARCHAR, "+Constants.SUB_PLAY_COUNT +" INTEGER, "+Constants.SUB_TRACKS_COUNT +" INTEGER, "+Constants.SUB_AUTHOR_NAME +" VARCHAR, "+Constants.SUB_ALBUM_ID +" INTEGER )";
        db.execSQL(subTbSql);
        //创建历史记录表
        String historyTbSql="CREATE TABLE " + Constants.HISTORY_TB_NAME + " ( "
                +Constants.HISTORY_ID+" INTEGER primary key autoincrement, "
                +Constants.HISTORY_TRACK_ID +" INTEGER, "
                +Constants.HISTORY_TITLE +" VARCHAR, "
                +Constants.HISTORY_PLAY_COUNT +" INTEGER, "
                +Constants.HISTORY_DURATION +" INTEGER, "
                +Constants.HISTORY_UPDATE_TIME +" INTEGER, "
                +Constants.HISTORY_COVER +" VARCHAR, "
                +Constants.HISTORY_AUTHOR +" VARCHAR "+")";
        db.execSQL(historyTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
