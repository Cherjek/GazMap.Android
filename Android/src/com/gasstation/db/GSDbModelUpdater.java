package com.gasstation.db;

import com.gasstation.MainActivity;
import com.gasstation.model.GPoint;

import android.content.Context;

public class GSDbModelUpdater {

	private Context mContext;
	private GSDbAdapter mDbAdapter;
	
	public enum TypeUpdate { Insert, Update, Delete }
	
	public GSDbModelUpdater (Context context) {
		this.mContext = context;	
		this.mDbAdapter = ((MainActivity)context).getDbAdapter();
	}
	
	public void Update (GPoint point, TypeUpdate type) {		
		if (type == TypeUpdate.Insert) {
			mDbAdapter.createRow(point.id, 
					point.lat, 
					point.lng, 
					point.title, 
					"",
					point.address,
					point.statusId,
					point.typeId,
					point.schedule,
					point.isBankCard,
					point.prices,
					point.vote,
					point.rating,
					point.voteCount
					);   
		} 
		else if (type == TypeUpdate.Update) {
			mDbAdapter.updateRow(point.id, 
					point.lat, 
					point.lng, 
					point.title, 
					"",
					point.address,
					point.statusId,
					point.typeId,
					point.schedule,
					point.isBankCard,
					point.prices,
					point.vote,
					point.rating,
					point.voteCount
					);
		}
		else if (type == TypeUpdate.Delete) {
			mDbAdapter.deleteRow(point.id);
		}
	}
}
