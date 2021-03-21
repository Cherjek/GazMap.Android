package com.gasstation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GSDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "applicationdata";
	private static final int DATABASE_VERSION = 1;
	public static final String DATATABLE_NAME = "dbgasstation";

	// запрос на создание базы данных
	private static final String DATABASE_CREATE = "create table " + DATATABLE_NAME + " (id integer primary key, "
			+ "lat real, lng real, title text, date text);";

	public GSDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// метод вызывается при создании базы данных
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	// метод вызывается при обновлении базы данных, например, когда вы увеличиваете номер версии базы данных
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(GSDatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + DATATABLE_NAME);
		onCreate(database);
	}
}
