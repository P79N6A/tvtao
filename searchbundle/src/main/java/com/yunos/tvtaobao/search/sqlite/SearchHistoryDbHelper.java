package com.yunos.tvtaobao.search.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xtt
 * on 2018/12/10
 * desc
 */
public class SearchHistoryDbHelper extends SQLiteOpenHelper {
    // 数据库名称
    public static String DB_NAME = "search_history";

    private static String DB_NAME_DB = "search_history.db";

    //历史搜索值在数据库中的字段
    public static String DB_NAME_HISTORY = "history";

    // 数据库版本
    private static int DB_VERSION = 1;

    public static final String CREATE_SEARCH_HISTORY ="create table search_history(_id integer primary key autoincrement,history text)" ;

    public SearchHistoryDbHelper(Context context) {
        super(context, DB_NAME_DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库的同时创建search_history表
        db.execSQL(CREATE_SEARCH_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
