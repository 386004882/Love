package com.example.jiji.love.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.jiji.love.Utils.MsgDatabaseHelper.CREATE_MSG;

/**
 * Created by jiji on 2017/4/21.
 * 聊天页面
 * 会话列表数据
 */

public class ContractDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_Con = "create table Contract("
            + "id integer primary key autoincrement,"
            + "name.text,"
            + "topMessage text,"
            + "time text)";
    Context mContext;

    public ContractDatabaseHelper(Context context, String name
            , SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MSG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
