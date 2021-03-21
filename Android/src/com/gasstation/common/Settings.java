package com.gasstation.common;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
	public final static String FILE_NAME = "gasstation";
	public final static String IP_ADDRESS = "IP_ADDRESS";
	public final static String TIME_UPDATE = "TIME_UPDATE";
	public final static String CURRENT_SELECTED_GAZ = "CURRENT_SELECTED_GAZ";
	
	Context context;
	
	public Settings(Context context)
	{
		this.context = context;
	}
	
	public int getInt(String name, int defValue)
	{
		SharedPreferences sp = context.getSharedPreferences(Settings.FILE_NAME, context.MODE_PRIVATE);
		return sp.getInt(name, defValue);
	}
	public String getString(String name, String defValue)
	{
		SharedPreferences sp = context.getSharedPreferences(Settings.FILE_NAME, context.MODE_PRIVATE);
		return sp.getString(name, defValue);
	}
	public Boolean getBoolean(String name, Boolean defValue)
	{
		SharedPreferences sp = context.getSharedPreferences(Settings.FILE_NAME, context.MODE_PRIVATE);
		return sp.getBoolean(name, defValue);	
	}
	
	public void putInt(String name, int value)
	{
		SharedPreferences sp = context.getSharedPreferences(Settings.FILE_NAME, context.MODE_PRIVATE);
     	SharedPreferences.Editor editor = sp.edit();
     	editor.putInt(name, value);
     	editor.commit();
	}
	public void putString(String name, String value)
	{
		SharedPreferences sp = context.getSharedPreferences(Settings.FILE_NAME, context.MODE_PRIVATE);
     	SharedPreferences.Editor editor = sp.edit();
     	editor.putString(name, value);
     	editor.commit();
	}
	public void putBoolean(String name, Boolean value)
	{
		SharedPreferences sp = context.getSharedPreferences(Settings.FILE_NAME, context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = sp.edit();
	    editor.putBoolean(name, value);
	    editor.commit();		
	}
}
