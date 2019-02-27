package com.yunos.tvtaobao.detailbundle.flash;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyConcernSQLite extends SQLiteOpenHelper {
	
	private static MyConcernSQLite sInstance = null;
	
	private static final String DATABASE_NAME = "myconcern.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String TABLE_NAME = "myconcern";
	/**商品ID*/
	public static final String ITEM_ID = "itemId";
	/**闹钟提醒时间*/
	public static final String REMIND_TIME = "remindTime";
//	public static final String START_TIME = "start_time";
	
	private MyConcernSQLite(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static MyConcernSQLite getInstance(Context context){
		if(sInstance == null){
			synchronized (MyConcernSQLite.class) {
				if(sInstance == null){
					sInstance = new MyConcernSQLite(context);
				}
			}
		}
		return sInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table " + TABLE_NAME + "(" + ITEM_ID
				+ " text primary key not null,"
				+ REMIND_TIME + " LONGBLOB not null);");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

}
