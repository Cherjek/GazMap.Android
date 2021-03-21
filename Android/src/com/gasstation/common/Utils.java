package com.gasstation.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utils {
	
	public static final Date getCurrentDate() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Date date = null;
		try {
			date = calendar.getTime();
    	} 
    	catch (IllegalArgumentException e) {
    		e.printStackTrace();
    	}
		
		return date;
	}

	public static final Object getCurrentTimeStamp() {
		Object timestamp = null;
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
		try {	
			timestamp = df.parse(df.format(getCurrentDate())).getTime();
    	} 
    	catch (ParseException e) {
    		e.printStackTrace();
    	}
		
		return timestamp;
	}
	
	public static final String getDecimalValueFormat(String format, Object value) {
		DecimalFormat df = new DecimalFormat(format);
		DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
		df.setDecimalFormatSymbols(dfs);
		return df.format(value);
	}
	
	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null)
	        return;

	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
	    int totalHeight = 0;
	    View view = null;
	    ((IListViewAdapter)listAdapter).stopUpdate(true);
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0)
	            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ((IListViewAdapter)listAdapter).stopUpdate(false);
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}
	
	public static final String getTime() {
		
		Date localDate = new Date();
	    return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(localDate);
	}
}
