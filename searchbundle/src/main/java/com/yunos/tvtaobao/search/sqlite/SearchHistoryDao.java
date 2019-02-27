package com.yunos.tvtaobao.search.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by xtt
 * on 2018/12/10
 * desc
 */
public class SearchHistoryDao {

    private SQLiteDatabase db;


    public SearchHistoryDao(Context context) {

        SearchHistoryDbHelper helper = new SearchHistoryDbHelper(context);
        db = helper.getWritableDatabase();

    }

    /**
     * 插入,返回值为行号
     * String sql = "insert into person(name,age) values('rose',21)";
     * db.execSQL(sql);
      */

    public long insert(String table, ContentValues values) {
//        删除相同的
        delete(table, "history = ?", new String[]{values.get(SearchHistoryDbHelper.DB_NAME_HISTORY).toString()});
//        超过24行删除最早的一行
        db.execSQL("delete from search_history where (select count(1) from search_history) > 24 and  (select min(_id) from search_history) = _id");
        return db.insert(table, null, values);
    }

    /**
     *  更新，返回值为影响数据表的行数
     *  String sql = "update person set age=21 where _id=1";
     *  db.execSQL(sql);
     *  表名
     *  values,修改后的值
     *  whereClause:条件语句,可以使用占位符，？
     *  whereArgs:使用数组中的值替换占位符
     *
      */

    public int update(String table, ContentValues values, String whereClause,
                      String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    /**
     *  删除,返回影响表的行数
     *  String sql = "delete from person where _id=1";
     *  db.execSQL(sql);
     */

    public int delete(String table, String whereClause, String[] whereArgs) {

        return db.delete(table, whereClause, whereArgs);
    }

    /**
     *  查询
     *  返回值，Cursor，游标，可以比作结果集
     *  1.sql语句，查询语句，可以包含条件,sql语句不用使用分号结尾，系统自动添加
     *  2.selectionArgs,sql的查询条件可以使用占位符，占位符可以使用selectionArgs替代
     *  select * from person where name=?
     */
    public Cursor select1(String sql, String[] selectionArgs) {

        return db.rawQuery(sql, selectionArgs);
    }


    /**
     * 查询
     * distinct：消除重复数据（去掉重复项）
     * 1、table，表名
     * 2、columns，查询的列（字段）
     * 3、selection：where后的条件子句，可以使用占位符
     * 4、selectionArgs,替换占位符的值，
     * 5、groupBy：根据某个字段进行分组
     * 6、having：分组之后再进一步过滤
     * 7、orderby:排序
     * limit：进行分页查询
     */

    public Cursor findAll(String table, String[] columns, String selection,
                          String[] selectionArgs, String groupBy, String having,
                          String orderBy) {
        return db.query(table, columns, selection, selectionArgs, groupBy,
                having, orderBy);
    }


}
