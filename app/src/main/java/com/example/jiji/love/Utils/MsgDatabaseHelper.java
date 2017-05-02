package com.example.jiji.love.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jiji on 2017/4/18.
 * 聊天页面
 * 聊天消息列表数据
 */

public class MsgDatabaseHelper extends SQLiteOpenHelper {

    //消息表
    public static final String CREATE_MSG = "create table Msg("
            + "id integer primary key autoincrement,"
            + "sender text,"
            + "time text,"
            + "type integer,"
            + "content text)";
    //会话表
    public static final String CREATE_Con = "create table Contract("
            + "id integer primary key autoincrement,"
            + "name text,"
            + "topMessage text,"
            + "time text)";
    Context mContext;

    public MsgDatabaseHelper(Context context, String name
            , SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MSG);
        db.execSQL(CREATE_Con);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
