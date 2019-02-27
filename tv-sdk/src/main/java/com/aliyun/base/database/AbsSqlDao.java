package com.aliyun.base.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsSqlDao<T> {

	protected String mTableName;

	private final static byte[] _writeLock = new byte[0];
	
	protected abstract SQLiteOpenHelper getSQLiteHelper();
	
	public AbsSqlDao(String tableName) {
		mTableName = tableName;
	}

	public void addTextColum(String columName) {
		exeSQL("ALTER TABLE '" + mTableName + "' ADD '" + columName + "' TEXT;");
	}

	public void addIntegerColum(String columName) {
		exeSQL("ALTER TABLE '" + mTableName + "' ADD '" + columName + "' INTEGER DEFAULT 0;");
	}

	public void addRealColum(String columName) {
		exeSQL("ALTER TABLE '" + mTableName + "' ADD '" + columName + "' REAL DEFAULT 0;");
	}

	public void addBlobColum(String columName) {
		exeSQL("ALTER TABLE '" + mTableName + "' ADD '" + columName + "' BLOB  DEFAULT null;");
	}
	
	/**
	 * 添加一行数据
	 * 
	 * @param values
	 * @return
	 */
	public long insert(ContentValues values) {
		synchronized (_writeLock) {
			long ret = -1;
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				ret = db.insert(mTableName, null, values);
			} catch (Exception e) {
				Log.w(mTableName, "insert(ContentValues values)", e);
			} finally {
				try {
					if (db != null) {
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}
			}
			return ret;
		}
	}
	
	public void insert(ContentValues[] values) {
		insert(values, 0, values.length);
	}

	/**
	 * 添加多行数据
	 * 
	 * @param values
	 */
	public void insert(ContentValues[] values, int start, int length) {
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				db.beginTransaction();
				for (int i = start; i < start + length; i++) {
					db.insert(mTableName, null, values[i]);
					db.yieldIfContendedSafely();
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				Log.w(mTableName, "insert(ContentValues[] values, int start, int length)", e);
			} finally {
				try {
					if (db != null) {
						db.endTransaction();
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}

			}
		}
	}
	
	/**
	 * 替换操作，若存在作update操作，若不存在作插入操作
	 * 
	 * @param table the table in which to replace the row
	 * @param nullColumnHack optional; may be null. SQL doesn't allow inserting a completely empty row without naming at
	 *    least one column name. If your provided initialValues is empty, no column names are known and an empty row can't 
	 *    be inserted. If not set to null, the nullColumnHack parameter provides the name of nullable column name to explicitly 
	 *    insert a NULL into in the case where your initialValues is empty.
	 * @param initialValues this map contains the initial column values for the row.
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	public long replace(ContentValues initialValues) {
		synchronized (_writeLock) {
			long ret = -1;
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				ret = db.replace(mTableName, null, initialValues);
			} catch (Exception e) {
				Log.w(mTableName, "replace(ContentValues initialValues)", e);
			} finally  {
				try {
					if (db != null) {
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}
			}
			return ret;
		}
	}
	
	public void replace(ContentValues[] values) {
		replace(values, 0, values.length);
	}
	
	public void replace(ContentValues[] values, int start, int length) {
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				db.beginTransaction();
				for (int i = start; i < start + length; i++) {
					db.replace(mTableName, null, values[i]);
					db.yieldIfContendedSafely();
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				Log.w(mTableName, "replace(ContentValues[] values, int start, int length)", e);
			} finally {
				try {
					if (db != null) {
						db.endTransaction();
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}

			}
		}
	}
	
	public long delete(String where) {
		return delete(where, null);
	}

	/**
	 * 
	 * @param where
	 * @param args
	 * @return 共删除了几行，如果删除失败返回0
	 */
	public long delete(String where, String[] args) {
		synchronized (_writeLock) {
			long ret = 0;
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				ret = db.delete(mTableName, where, args);
			} catch (Exception e) {
				Log.w(mTableName, "delete(String where, String[] args) ", e);
			} finally {
				try {
					if (db != null) {
						db.close();
						db = null;
					}

				} catch (Exception e) {
				}

			}
			return ret;
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param values
	 *            新的值
	 * @param whereClause
	 *            where语句，允许带?形式
	 * @param whereArgs
	 *            相应的?的值
	 * @return
	 */
	public long update(ContentValues values, String whereClause, String[] whereArgs) {
		synchronized (_writeLock) {
			long ret = -1;
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				ret = db.update(mTableName, values, whereClause, whereArgs);
			} catch (Exception e) {
				Log.w(mTableName, "update(ContentValues values, String whereClause, String[] whereArgs)", e);
			} finally {
				try {
					if (db != null) {
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}

			}
			return ret;
		}
	}
	
	public void update(ContentValues[] values, String[] whereClause) {
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				db.beginTransaction();
				for (int i = 0; i < values.length; ++i) {
					db.update(mTableName, values[i], whereClause[i], null);
					db.yieldIfContendedSafely();
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				Log.w(mTableName, "update(ContentValues[] values, String[] whereClause)", e);
			} finally {
				try {
					if (db != null) {
						db.endTransaction();
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * @param columns
	 *            A list of which columns to return. Passing null will return
	 *            all columns, which is discouraged to prevent reading data from
	 *            storage that isn't going to be used.
	 * 
	 * @param whereClause
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            WHERE clause (excluding the WHERE itself). Passing null will
	 *            return all rows for the given table.
	 * @param whereArgs
	 *            You may include ?s in selection, which will be replaced by the
	 *            values from selectionArgs, in order that they appear in the
	 *            selection. The values will be bound as Strings.
	 * @param groupBy
	 *            A filter declaring how to group rows, formatted as an SQL
	 *            GROUP BY clause (excluding the GROUP BY itself). Passing null
	 *            will cause the rows to not be grouped.
	 * @param having
	 *            A filter declare which row groups to include in the cursor, if
	 *            row grouping is being used, formatted as an SQL HAVING clause
	 *            (excluding the HAVING itself). Passing null will cause all row
	 *            groups to be included, and is required when row grouping is
	 *            not being used.
	 * @param orderBy
	 *            How to order the rows, formatted as an SQL ORDER BY clause
	 *            (excluding the ORDER BY itself). Passing null will use the
	 *            default sort order, which may be unordered.
	 * @return
	 */
	public T queryForObject(String[] columns, String whereClause, String[] whereArgs, String groupBy, String having, String orderBy) {
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				cursor = db.query(mTableName, columns, whereClause, whereArgs, groupBy, having, orderBy);
				if (cursor != null && cursor.moveToNext()) {
					T obj = cursorRowToObject(cursor);
					return obj;
				}
			} catch (Exception e) {
				Log.w(mTableName, "queryForObject", e);
			} finally {
				try {
					if (cursor != null) {
						cursor.close();
						cursor = null;
					}
					if (db != null) {
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}

			}
		}
		return null;
	}

	/**
	 * 
	 * @param columns
	 *            A list of which columns to return. Passing null will return
	 *            all columns, which is discouraged to prevent reading data from
	 *            storage that isn't going to be used.
	 * 
	 * @param whereClause
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            WHERE clause (excluding the WHERE itself). Passing null will
	 *            return all rows for the given table.
	 * @param whereArgs
	 *            You may include ?s in selection, which will be replaced by the
	 *            values from selectionArgs, in order that they appear in the
	 *            selection. The values will be bound as Strings.
	 * @param groupBy
	 *            A filter declaring how to group rows, formatted as an SQL
	 *            GROUP BY clause (excluding the GROUP BY itself). Passing null
	 *            will cause the rows to not be grouped.
	 * @param having
	 *            A filter declare which row groups to include in the cursor, if
	 *            row grouping is being used, formatted as an SQL HAVING clause
	 *            (excluding the HAVING itself). Passing null will cause all row
	 *            groups to be included, and is required when row grouping is
	 *            not being used.
	 * @param orderBy
	 *            How to order the rows, formatted as an SQL ORDER BY clause
	 *            (excluding the ORDER BY itself). Passing null will use the
	 *            default sort order, which may be unordered.
	 * @return 如果没有数据，则返回非null的list, list.size()=0
	 */
	public List<T> queryForList(String[] columns, String whereClause, String[] whereArgs, String groupBy, String having, String orderBy) {
		List<T> list = new ArrayList<T>();
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				cursor = db.query(mTableName, columns, whereClause, whereArgs, groupBy, having, orderBy);
				if (cursor != null && cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						list.add(cursorRowToObject(cursor));
					}
				}
			} catch (Exception e) {
				Log.w(mTableName, "queryForList", e);
			} finally {
				try {
					if (cursor != null) {
						cursor.close();
						cursor = null;
					}
					if (db != null) {
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}

			}
		}
		return list;
	}

	/**
	 * 执行多条sql语句，采用事务处理
	 * 
	 * @param sqls
	 */
	public void exeSQLs(String[] sqls) {
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				db.beginTransaction();
				for (String sql : sqls) {
					db.execSQL(sql);
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				Log.w(mTableName, "exeSQLs", e);
			} finally {
				try {
					if (db != null) {
						db.endTransaction();
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}

			}
		}
	}

	public void clear() {
		synchronized (_writeLock) {
			exeSQL("delete from " + mTableName + ";");
		}
	}

	public void exeSQL(String sql) {
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				db.execSQL(sql);
			} catch (Exception e) {
				Log.w(mTableName, "exeSQLs", e);
			} finally {
				try {
					if (db != null) {
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}

			}
		}

	}

	/**
	 * 
	 * @param sql
	 * @param bindArgs
	 *            only byte[], String, Long and Double are supported in
	 *            bindArgs.
	 */
	public void exeSQL(String sql, Object[] bindArgs) {
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				db.execSQL(sql, bindArgs);
			} catch (Exception e) {
				Log.w(mTableName, "exeSQLs", e);
			} finally {
				try {
					if (db != null) {
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}

			}
		}
	}

	/**
	 * 
	 * @param sql
	 * @param bindArgs
	 *            only byte[], String, Long and Double are supported in
	 *            bindArgs.
	 * @return if this insert is successful. -1 otherwise.
	 */
	public long insert(String sql, Object[] bindArgs) {
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			SQLiteStatement stmt = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				stmt = db.compileStatement(sql);
				if (bindArgs != null) {
					for (int i = 0, numArgs = bindArgs.length; i < numArgs; i++) {
						DatabaseUtils.bindObjectToProgram(stmt, i + 1, bindArgs[i]);
					}
				}
				return stmt.executeInsert();
			} catch (Exception e) {
				Log.w(mTableName, "insert(String sql, Object[] bindArgs)", e);
			} finally {
				try {
					if (stmt != null) {
						stmt.close();
						stmt = null;
					}
					if (db != null) {
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}

			}
			return -1;
		}
	}

	public List<String> getColumnNames() {
		synchronized (_writeLock) {
			List<String> ret = new ArrayList<String>();
			SQLiteDatabase db = getSQLiteHelper().getWritableDatabase();
			Cursor cursor = db.query(mTableName, null, null, null, "", "", "");
			if (cursor != null) {
				for (String name : cursor.getColumnNames()) {
					ret.add(name);
				}
				cursor.close();
				cursor = null;
				return ret;
			}
			db.close();
			db = null;
		}
		return null;
	}

	public int getDataCount() {
		synchronized (_writeLock) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
				db = getSQLiteHelper().getWritableDatabase();
				cursor = db.rawQuery("select count(*) from " + mTableName, null);
				if (cursor != null && cursor.moveToNext()) {
					return cursor.getInt(0);
				}
				return 0;
			} catch (Exception e) { 
				Log.w(mTableName, "getDataCount", e);
			} finally {
				try {
					if (cursor != null) {
						cursor.close();
						cursor = null;
					}
					if (db != null) {
						db.close();
						db = null;
					}
				} catch (Exception e) {
				}
			}
		}
		return 0;
	}

	/**
	 * 将cursor的一行转成一个实体对象
	 * 
	 * @param cursor
	 * @return
	 */
	public T cursorRowToObject(Cursor cursor) {
		return null;
	}
}
