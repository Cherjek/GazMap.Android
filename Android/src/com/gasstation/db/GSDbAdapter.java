package com.gasstation.db;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.gasstation.calculator.FuelItem;
import com.gasstation.model.PriceTab;

public class GSDbAdapter {
	
	public static final String KEY_ROWID = "id";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";	
	public static final String KEY_TITLE = "title";
	public static final String KEY_DATE = "date";
	
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_STATUS = "statusId";
	public static final String KEY_TYPE = "typeId";
	public static final String KEY_SCHEDULE = "schedule";	 
	public static final String KEY_ISBANKCARD = "isBankCard";	
	public static final String KEY_VOTE = "vote";//рейтинг пользователя
	public static final String KEY_RATING = "rating";//рейтинг общий станции
	public static final String KEY_VOTE_COUNT = "voteCount";//число голосов
	
	public static final String KEY_PRICE = "price";
	
	private Context context;
	private SQLiteDatabase database;
	private GSDatabaseHelper dbHelper;

	public GSDbAdapter(Context context) {
		this.context = context;
	}	
	
	public GSDbAdapter open() throws SQLException {
		dbHelper = GSDatabaseHelper.getInstance(context);
		database = dbHelper.db();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public long createRow(long id, Double lat, Double lng, String title, String date,
			String address, Integer statusId, Long typeId, String schedule, Integer isBankCard,
			PriceTab[] prices, Integer vote, Double rating, Integer voteCount) throws SQLException {
		ContentValues initialValues = createContentValues(id, lat, lng, title, date, 
				address, statusId, typeId, schedule, isBankCard,
				vote, rating, voteCount);		
		
		long result = -1;
		
		database.beginTransaction();
		try {
			result = database.insert(GSDatabaseHelper.DATATABLE_NAME, null, initialValues);
			if (result > 0 && prices != null) {
				for (int i = 0; i < prices.length; i++) {
					ContentValues initialPriceValues = createPriceContentValues(result, prices[i].TypeId, (Double)prices[i].Value, prices[i].Date);
					database.insert(GSDatabaseHelper.DATATABLE_PRICE_NAME, null, initialPriceValues);
				}
			}
			database.setTransactionSuccessful();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			database.endTransaction();
		}

		return result;
	}

	public boolean updateRow(long id, Double lat, Double lng, String title, String date,
			String address, Integer statusId, Long typeId, String schedule, Integer isBankCard,
			PriceTab[] prices, Integer vote, Double rating, Integer voteCount) throws SQLException {
		ContentValues updateValues = createContentValues(id, lat, lng, title, date,
				address, statusId, typeId, schedule, isBankCard,
				vote, rating, voteCount);
		
		database.beginTransaction();
		try {
			database.update(GSDatabaseHelper.DATATABLE_NAME, updateValues, KEY_ROWID + " = " + id, null);
			if (prices != null) {				
				deletePriceRow(id);
				for (int i = 0; i < prices.length; i++) {
					ContentValues initialPriceValues = createPriceContentValues(id, prices[i].TypeId, (Double)prices[i].Value, prices[i].Date);
					database.insert(GSDatabaseHelper.DATATABLE_PRICE_NAME, null, initialPriceValues);
				}
			}
			database.setTransactionSuccessful();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			database.endTransaction();
		}

		return true;
	}
	
	public boolean deletePriceRow(long id) throws SQLException {
		return database.delete(GSDatabaseHelper.DATATABLE_PRICE_NAME, KEY_ROWID + " = " + id, null) > 0;
	}
	
	public boolean deleteRow(long id) throws SQLException {
		database.beginTransaction();
		try {
			
			deletePriceRow(id);		
			database.delete(GSDatabaseHelper.DATATABLE_NAME, KEY_ROWID + " = " + id, null);
			database.setTransactionSuccessful();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
			database.endTransaction();
		}
		
		return true;
	}
	
	public Cursor fetchAllRows() throws SQLException {
		return database.query(GSDatabaseHelper.DATATABLE_NAME, new String[] { KEY_ROWID,
				KEY_LAT, KEY_LNG, KEY_TITLE, KEY_DATE, KEY_ADDRESS, KEY_STATUS, KEY_TYPE, KEY_SCHEDULE, KEY_ISBANKCARD,
				KEY_VOTE, KEY_RATING, KEY_VOTE_COUNT }, 
				null, null, null, null, null);
	}
	
	public Cursor fetchTodo(long id) throws SQLException {
		Cursor mCursor = database.query(true, GSDatabaseHelper.DATATABLE_NAME, new String[] {
				KEY_ROWID, KEY_LAT, KEY_LNG, KEY_TITLE, KEY_DATE, KEY_ADDRESS, KEY_STATUS, KEY_TYPE, KEY_SCHEDULE, KEY_ISBANKCARD,
				KEY_VOTE, KEY_RATING, KEY_VOTE_COUNT },
				KEY_ROWID + " = " + id, null, null, null, null, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor fetchTodo(String filter) throws SQLException {
		Cursor mCursor = database.query(true, GSDatabaseHelper.DATATABLE_NAME, new String[] {
				KEY_ROWID, KEY_LAT, KEY_LNG, KEY_TITLE, KEY_DATE, KEY_ADDRESS, KEY_STATUS, KEY_TYPE, KEY_SCHEDULE, KEY_ISBANKCARD,
				KEY_VOTE, KEY_RATING, KEY_VOTE_COUNT },
				filter, null, null, null, null, null);
		
		return mCursor;
	}
	
	public Cursor fetchPrices(long id) throws SQLException {
		Cursor mCursor = database.query(true, GSDatabaseHelper.DATATABLE_PRICE_NAME, new String[] {
				KEY_ROWID, KEY_TYPE, KEY_PRICE, KEY_DATE },
				KEY_ROWID + " = " + id, null, null, null, null, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor fetchPricesType(long id, long typeId) throws SQLException {
		Cursor mCursor = database.query(true, GSDatabaseHelper.DATATABLE_PRICE_NAME, new String[] {
				KEY_ROWID, KEY_TYPE, KEY_PRICE, KEY_DATE },
				KEY_ROWID + " = " + id + " AND " + KEY_TYPE + " = " + typeId, null, null, null, null, null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
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

	private ContentValues createContentValues(long id, Double lat, Double lng, String title, String date,
			String address, Integer statusId, Long typeId, String schedule, Integer isBankCard,
			Integer vorte, Double rating, Integer voteCount) {
		ContentValues values = new ContentValues();
		values.put(KEY_ROWID, id);
		if (lat != null) values.put(KEY_LAT, lat);
		if (lng != null) values.put(KEY_LNG, lng);
		if (title != null) values.put(KEY_TITLE, title);
		if (date != null) values.put(KEY_DATE, date);
		
		if (address != null) values.put(KEY_ADDRESS, address);
		if (statusId != null) values.put(KEY_STATUS, statusId);
		if (typeId != null) values.put(KEY_TYPE, typeId);
		if (schedule != null) values.put(KEY_SCHEDULE, schedule);
		if (isBankCard != null) values.put(KEY_ISBANKCARD, isBankCard);
		
		if (vorte != null) values.put(KEY_VOTE, vorte);
		if (rating != null) values.put(KEY_RATING, rating);
		if (voteCount != null) values.put(KEY_VOTE_COUNT, voteCount);
		return values;
	}
	
	private ContentValues createPriceContentValues(long id, Long typeId, Double price, String date) {
		ContentValues values = new ContentValues();
		values.put(KEY_ROWID, id);
		
		if (typeId != null) values.put(KEY_TYPE, typeId);		
		if (price != null) values.put(KEY_PRICE, price);
		if (date != null) values.put(KEY_DATE, date);
		return values;
	}
	
	public void insertFuelData(double fueled, double price, double drived) {
		ContentValues values = new ContentValues();
		values.put("date", (new Date()).getTime());
		values.put("price", price);
		values.put("fueled", fueled);
		values.put("drived", drived);
		database.insert(GSDatabaseHelper.DATATABLE_FUEL_NAME, null, values);
	}
	
	public void updateFuelData(FuelItem item) {
		ContentValues values = new ContentValues();
		values.put("date", item.getDate().getTime());
		values.put("price", item.getPrice());
		values.put("fueled", item.getFueled());
		values.put("drived", item.getFueled());
		database.update(GSDatabaseHelper.DATATABLE_FUEL_NAME, values, "id = ?", new String[]{Long.toString(item.getId())});
	}
	
	public double getLastFueledPrice() {
		double price = 0.0;
		Cursor mCursor = database.rawQuery("select price from " + GSDatabaseHelper.DATATABLE_FUEL_NAME + " ORDER BY Date DESC LIMIT 1", null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			price = mCursor.getDouble(0);
		}
		return price;
	}
	
	public ArrayList<FuelItem> getAllFuelItems() throws SQLException {
		Cursor mCursor = database.query(true, GSDatabaseHelper.DATATABLE_FUEL_NAME, new String[] {
				"id", "date", "price", "fueled", "drived"},
				null, null, null, null, "date DESC", null);
		
		ArrayList<FuelItem> list = new ArrayList<FuelItem>();
		
		if (mCursor != null) {
			mCursor.moveToFirst();
			while(mCursor.isAfterLast() == false) {
				long id = mCursor.getLong(0);
				Date date = new Date(mCursor.getLong(1));
				double price = mCursor.getDouble(2);
				double fueled = mCursor.getDouble(3);
				double drived = mCursor.getDouble(4);
				list.add(new FuelItem(id, date, fueled, price, drived));
				mCursor.moveToNext();
			}
		}
		return list;
	}
	
	public void updateGazolinePrice(double price) {
		ContentValues values = new ContentValues();
		values.put("price", price);
		database.delete(GSDatabaseHelper.DATATABLE_GAZOLINE_NAME, null, null);
		database.insert(GSDatabaseHelper.DATATABLE_GAZOLINE_NAME, null, values);
	}
	
	public double getGazolinePrice() {
		double price = 0.0;
		Cursor mCursor = database.rawQuery("select price from " + GSDatabaseHelper.DATATABLE_GAZOLINE_NAME, null);
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			price = mCursor.getDouble(0);
		}
		return price;
	}
}
