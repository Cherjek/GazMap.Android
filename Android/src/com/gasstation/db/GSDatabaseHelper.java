package com.gasstation.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GSDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "applicationdata";
	private static final int DATABASE_VERSION = 3;
	public static final String DATATABLE_NAME = "dbgasstation";
	public static final String DATATABLE_PRICE_NAME = "dbgasstation_price";
	public static final String DATATABLE_FUEL_NAME = "dbgasstation_fuel";
	public static final String DATATABLE_GAZOLINE_NAME = "dbgasstation_gazoline";

	// запрос на создание базы данных
	private static final String MAIN_TABLE_CREATE = "create table " + DATATABLE_NAME + " (id integer primary key, "
			+ "lat real, lng real, title text, date text, address text, statusId integer, typeId integer, schedule text, isBankCard integer," +
			"vote integer, rating integer, voteCount integer);";
	
	private static final String TABLE_PRICE_CREATE = "create table " + DATATABLE_PRICE_NAME + " (id integer, "
			+ "typeId integer, price real, date text);";
	
	private static final String TABLE_FUEL_CREATE = "create table " + DATATABLE_FUEL_NAME + " (id integer PRIMARY KEY AUTOINCREMENT NOT NULL, "
			+ "date integer, price real, fueled real, drived real);";
	
	private static final String TABLE_GAZOLINE_CREATE = "create table " + DATATABLE_GAZOLINE_NAME + " (price real);";

	private static GSDatabaseHelper _instance;
	private SQLiteDatabase _db;
	
	private GSDatabaseHelper(Context context) {
		// use getInstance()
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static GSDatabaseHelper getInstance(Context context) {
        if (_instance == null) {
            _instance = new GSDatabaseHelper(context);
        }
        _instance.openDatabase();
        return _instance;
    }
	
	private void openDatabase() {
        if (_db == null || _db.isOpen() == false) {
            _db = getWritableDatabase();
        }
    }
	
	public SQLiteDatabase db() {
		return _db;
	}

    public void closeDatabase() {
        if (_db != null)
            _db.close();
    }

	// метод вызывается при создании базы данных
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(MAIN_TABLE_CREATE);
		database.execSQL(TABLE_PRICE_CREATE);
		database.execSQL(TABLE_FUEL_CREATE);
		database.execSQL(TABLE_GAZOLINE_CREATE);
	}

	// метод вызывается при обновлении базы данных, например, когда вы увеличиваете номер версии базы данных
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(GSDatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + DATATABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + DATATABLE_PRICE_NAME);
		
		//WARNING don't delete DATATABLE_FUEL_NAME and TABLE_GAZOLINE_CREATE

		onCreate(database);
	}
}
