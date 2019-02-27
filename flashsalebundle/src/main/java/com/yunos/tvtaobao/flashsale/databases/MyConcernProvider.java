package com.yunos.tvtaobao.flashsale.databases;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;


public class MyConcernProvider extends ContentProvider {
	
	private MyConcernSQLite mMyConcernSQLite;
	private Context mContext;
	private SQLiteDatabase mMyConcernDB;
	
	public static final String AUTOHORITY = "com.yunos.flashsale.databases.myconcernprovider";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTOHORITY
			+ "/" + MyConcernSQLite.TABLE_NAME);
	
	private static final int MATCH_TABLE = 1;
	private static final int MATCH_ITEM = 2;
	
	private static final UriMatcher sMatcher;
	// 注册需要匹配的Uri
	static {
		// 实例化
		sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		// 实例化后调用addURI方法注册URI，该方法有三个参数，分别需要传入URI字符串的authority部分、path部分以及自定义的整数code三者；
		sMatcher.addURI(AUTOHORITY, MyConcernSQLite.TABLE_NAME, MATCH_TABLE);
		sMatcher.addURI(AUTOHORITY, MyConcernSQLite.TABLE_NAME + "/#", MATCH_ITEM);
	}

	@Override
	public boolean onCreate() {
		mContext = this.getContext();
		mMyConcernSQLite = MyConcernSQLite.getInstance(mContext);
		mMyConcernDB = mMyConcernSQLite.getWritableDatabase();  
        return (mMyConcernDB == null) ?  false : true; 
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		switch (sMatcher.match(uri)) {
			case MATCH_TABLE:
				mMyConcernDB.delete(MyConcernSQLite.TABLE_NAME, selection, selectionArgs);
				break;
			case MATCH_ITEM:
				String itemId = uri.getPathSegments().get(1);
				count = mMyConcernDB.delete(MyConcernSQLite.TABLE_NAME, MyConcernSQLite.ITEM_ID 
						+ " = '" + itemId + (!TextUtils.isEmpty(selection) ? "' AND (" + selection + ")" : "'"), selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI = " + uri);
		}
		return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowId = mMyConcernDB.insert(MyConcernSQLite.TABLE_NAME, null, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri,
					null);
			return noteUri;
		}
		return null;
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
		Cursor c;
		switch (sMatcher.match(uri)) {
		case MATCH_TABLE:
			c = mMyConcernDB.query(MyConcernSQLite.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
			break;
		case MATCH_ITEM:
			String itemId = uri.getPathSegments().get(1);
			c = mMyConcernDB.query(MyConcernSQLite.TABLE_NAME, projection, MyConcernSQLite.ITEM_ID + " = '" + itemId
					+ (!TextUtils.isEmpty(selection) ? "' AND (" + selection + ")" : "'"), selectionArgs, null, null, sortOrder);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI = " + uri);
	    }
//		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
		int affected = 0;
		switch (sMatcher.match(uri)) {
		case MATCH_TABLE:
			mMyConcernDB.update(MyConcernSQLite.TABLE_NAME, values, selection, selectionArgs);
			break;
		case MATCH_ITEM:
			String itemId = uri.getPathSegments().get(1);
			mMyConcernDB.update(MyConcernSQLite.TABLE_NAME, values, MyConcernSQLite.ITEM_ID + " = '" + itemId
					+ (!TextUtils.isEmpty(selection) ? "' AND (" + selection + ")" : "'"), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI = " + uri);
	    }
		getContext().getContentResolver().notifyChange(uri, null);  
		return affected;
	}

}
