package com.gasstation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class GSDbAdapter {
	
	public static final String KEY_ROWID = "id";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";	
	public static final String KEY_TITLE = "title";	
	public static final String KEY_DATE = "date";
	
	private Context context;
	private SQLiteDatabase database;
	private GSDatabaseHelper dbHelper;

	public GSDbAdapter(Context context) {
		this.context = context;
	}	
	
	public GSDbAdapter open() throws SQLException {
		dbHelper = new GSDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long createRow(long id, Double lat, Double lng, String title, String date) throws SQLException {
		ContentValues initialValues = createContentValues(id, lat, lng, title, date);

		return database.insert(GSDatabaseHelper.DATATABLE_NAME, null, initialValues);
	}

	public boolean updateRow(long id, Double lat, Double lng, String title, String date) throws SQLException {
		ContentValues updateValues = createContentValues(id, lat, lng, title, date);

		return database.update(GSDatabaseHelper.DATATABLE_NAME, updateValues, KEY_ROWID + " = " + id, null) > 0;
	}
	
	public boolean deleteRow(long id) throws SQLException {
		return database.delete(GSDatabaseHelper.DATATABLE_NAME, KEY_ROWID + " = " + id, null) > 0;
	}
	
	public Cursor fetchAllRows() throws SQLException {
		return database.query(GSDatabaseHelper.DATATABLE_NAME, new String[] { KEY_ROWID,
				KEY_LAT, KEY_LNG, KEY_TITLE, KEY_DATE }, null, null, null,
				null, null);
	}
	
	public Cursor fetchTodo(long id) throws SQLException {
		Cursor mCursor = database.query(true, GSDatabaseHelper.DATATABLE_NAME, new String[] {
				KEY_ROWID, KEY_LAT, KEY_LNG, KEY_TITLE, KEY_DATE },
				KEY_ROWID + " = " + id, null, null, null, null, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
		
	public Cursor fetchTodo(String filter) throws SQLException {
		Cursor mCursor = database.query(true, GSDatabaseHelper.DATATABLE_NAME, new String[] {
				KEY_ROWID, KEY_LAT, KEY_LNG, KEY_TITLE, KEY_DATE },
				filter, null, null, null, null, null);
		
		return mCursor;
	}
	
	public Integer fetchMax() {
		
		Integer valMax = 0;
		Cursor mCursor = database.rawQuery("select MAX(" + KEY_ROWID + ") from " + GSDatabaseHelper.DATATABLE_NAME, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
			valMax = mCursor.getInt(0);
		}
		return valMax;
	}

	private ContentValues createContentValues(long id, Double lat, Double lng, String title, String date) {
		ContentValues values = new ContentValues();
		values.put(KEY_ROWID, id);
		if (lat != null) values.put(KEY_LAT, lat);
		if (lng != null) values.put(KEY_LNG, lng);
		if (title != null) values.put(KEY_TITLE, title);
		if (date != null) values.put(KEY_DATE, date);
		return values;
	}
}
