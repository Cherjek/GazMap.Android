package com.gasstation.calculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ToString {

	public static final String EMPTY = ""; 
	private static DecimalFormat mFormater;
	private static DecimalFormat mFormaterMoney;
	private static SimpleDateFormat mDateFormat = new SimpleDateFormat("MM.dd.yyyy");
	
	
	public static String money(double value) {
		if (mFormaterMoney == null) {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	        symbols.setGroupingSeparator(' ');
	        mFormaterMoney = new DecimalFormat("#,##0.00", symbols);
		}
		
		return mFormaterMoney.format(value);
	}
	
	public static String decimal(double value) {
		if (mFormater == null) {
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
	        symbols.setGroupingSeparator(' ');
	        mFormater = new DecimalFormat("#,##0.0", symbols);
		}
		
		return mFormater.format(value);
	}
	
	public static String date(Date value) {
		return mDateFormat.format(value);
	}
	
}
