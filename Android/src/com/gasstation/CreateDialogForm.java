package com.gasstation;


import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gasstation.common.Utils;
import com.gasstation.db.GSDbAdapter;
import com.gasstation.model.GPoint;
import com.gasstation.model.OilTypes;
import com.gasstation.model.PriceTab;

public class CreateDialogForm {
	
	private GPoint lastDragItemPoint;
	private GPoint point;
	private Context mContext;
	
	private TextView tvPricePoint;
	private ListView lstPrice;
	private GSDbAdapter mDbAdapter;
	
	public CreateDialogForm(GPoint point, Context context) {
		this.point = point;
		this.lastDragItemPoint = new GPoint();
		this.lastDragItemPoint.id = point.id;
		this.lastDragItemPoint.address = point.address;
		this.lastDragItemPoint.date = point.date;
		this.lastDragItemPoint.isBankCard = point.isBankCard;
		this.lastDragItemPoint.lat = point.lat;
		this.lastDragItemPoint.lng = point.lng;
		this.lastDragItemPoint.prices = point.prices;
		this.lastDragItemPoint.rating = point.rating;
		this.lastDragItemPoint.schedule = point.schedule;
		this.lastDragItemPoint.statusId = point.statusId;
		this.lastDragItemPoint.title = point.title;
		this.lastDragItemPoint.typeId = point.typeId;
		this.lastDragItemPoint.vote = point.vote;

		this.mContext = context;
		mDbAdapter = ((MainActivity)context).getDbAdapter();
	}
	
	public GPoint getEditPoint() {
		lastDragItemPoint.prices = getPriceArray(lastDragItemPoint.typeId);		
		return lastDragItemPoint;
	}
	
	private PriceTab[] getPriceArray(Long typeId) {
		CustomPriceAdapter adapterPrice = (CustomPriceAdapter) lstPrice.getAdapter();
		List<PriceTab> items = new ArrayList<PriceTab>();
		if (adapterPrice != null) {
			for(int i = 0; i < adapterPrice.getCount(); i++) {
				PriceTab item = adapterPrice.getItem(i);
				item.Value = item.Value == null ? 0.0 : Double.parseDouble(Utils.getDecimalValueFormat("#####0.00", item.Value).replace(',', '.'));
				items.add(item);
			}
		}
		if (items.size() == 0) {
			PriceTab item = new PriceTab(null, null, typeId, 0.0);
			items.add(item);
		}
		PriceTab[] results = new PriceTab[items.size()];
		items.toArray(results);
		
		return results;
	}
	
	public Dialog showCreatePointDialog() {
		final Dialog dlgCreatePoint = new Dialog(mContext);
		dlgCreatePoint.setContentView(R.layout.create_item_layout);
		if (lastDragItemPoint.id == null) dlgCreatePoint.setTitle("Добавление точки");
		else dlgCreatePoint.setTitle("Редактирование точки");
    	
    	final EditText textTitle = (EditText) dlgCreatePoint.findViewById(R.id.et_title_point);
    	textTitle.setText(lastDragItemPoint.title);
    	textTitle.addTextChangedListener(new TextWatcher() {

    	    public void onTextChanged(CharSequence s, int start, int before,
    	            int count) {
    	    	lastDragItemPoint.title = String.format("%s", s);
    	    }

    	    public void beforeTextChanged(CharSequence s, int start, int count,
    	            int after) {

    	    }

    	    public void afterTextChanged(Editable s) {

    	    }
    	});
    	
    	tvPricePoint = (TextView) dlgCreatePoint.findViewById( R.id.tv_price_point );
    	lstPrice = (ListView) dlgCreatePoint.findViewById(R.id.lst_price_point);    	
    	createListPrice();    	    	 
    	
    	int visibility = lastDragItemPoint.id == null ? View.GONE : View.VISIBLE;
    	
    	final EditText textAddress = (EditText) dlgCreatePoint.findViewById(R.id.et_address_point);
    	textAddress.setText(lastDragItemPoint.address);
    	textAddress.addTextChangedListener(new TextWatcher() {

    	    public void onTextChanged(CharSequence s, int start, int before,
    	            int count) {
    	    	lastDragItemPoint.address = textAddress.getText().toString();
    	    }

    	    public void beforeTextChanged(CharSequence s, int start, int count,
    	            int after) {

    	    }

    	    public void afterTextChanged(Editable s) {

    	    }
    	});
    	((TextView) dlgCreatePoint.findViewById(R.id.tv_address_point)).setVisibility(visibility);
    	textAddress.setVisibility(visibility);
    	
    	TextView tv_status_point = (TextView) dlgCreatePoint.findViewById(R.id.tv_status_point);
    	final Spinner spStatus = (Spinner) dlgCreatePoint.findViewById(R.id.sp_status_point);
    	
    	tv_status_point.setVisibility(visibility);
    	spStatus.setVisibility(visibility);
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, new String[] { "Работает", "Не работает" } );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spStatus.setAdapter(adapter);
    	if (lastDragItemPoint.statusId != null) {
    		spStatus.setSelection(lastDragItemPoint.statusId - 1);
    	}
    	spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    		public void onItemSelected(AdapterView<?> parent,
    				View itemSelected, int position, long id) {
    			
    			lastDragItemPoint.statusId = position + 1;
    		}
    		public void onNothingSelected(AdapterView<?> parent) {
    		}
    	});
    	if (lastDragItemPoint.id == null) lastDragItemPoint.statusId = 1;
    	
    	final Spinner spType = (Spinner) dlgCreatePoint.findViewById(R.id.sp_type_point);
    	ArrayList<String> oilTypes = OilTypes.GetStrOilTypes(lastDragItemPoint.id == null ||
    			(lastDragItemPoint.id != null && lastDragItemPoint.typeId < 4));
    	String[] values = new String[oilTypes.size()];
    	oilTypes.toArray(values);
    	adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, values );
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spType.setAdapter(adapter);
    	if (lastDragItemPoint.typeId != null) {
    		if (lastDragItemPoint.typeId >= 4) {
    			spType.setEnabled(false);
    		}
    		spType.setSelection(OilTypes.GetIndexFromTypeId(lastDragItemPoint.typeId));
    	}
    	spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    		public void onItemSelected(AdapterView<?> parent,
    				View itemSelected, int position, long id) {
    			
    			lastDragItemPoint.typeId = OilTypes.GetTypeIdFromIndex(position);
    			createListPrice();
    		}
    		public void onNothingSelected(AdapterView<?> parent) {
    		}
    	});
    	
    	final EditText textSchedule = (EditText) dlgCreatePoint.findViewById(R.id.et_schedule_point);
    	textSchedule.setText(lastDragItemPoint.schedule);
    	textSchedule.addTextChangedListener(new TextWatcher() {

    	    public void onTextChanged(CharSequence s, int start, int before,
    	            int count) {
    	    	lastDragItemPoint.schedule = textSchedule.getText().toString();
    	    }

    	    public void beforeTextChanged(CharSequence s, int start, int count,
    	            int after) {

    	    }

    	    public void afterTextChanged(Editable s) {

    	    }
    	});
    	
    	final CheckBox chckBank = (CheckBox) dlgCreatePoint.findViewById(R.id.chck_isBankCard_point);
    	if (lastDragItemPoint.isBankCard != null) {
    		chckBank.setChecked(lastDragItemPoint.isBankCard == 1);
    	}
    	chckBank.setOnClickListener(new OnClickListener() {
    		@Override
			public void onClick(View v) {
				lastDragItemPoint.isBankCard = ((CheckBox) v).isChecked() ? 1 : 0;
			}		
    	});   	
    	return dlgCreatePoint;
	}
	
	private Object getValuePrice(long id, long typeId) {
		Object value = null;
		if (mDbAdapter != null) {
			Cursor cursor = mDbAdapter.fetchPricesType(id, typeId);
			cursor.moveToFirst();
			while(!cursor.isAfterLast()) {
				value = cursor.getDouble(cursor.getColumnIndex(GSDbAdapter.KEY_PRICE));
				cursor.moveToNext();
			}
		}
		return value;
	}
	
	//переделать, на независимые значения
	private void createListPrice() {
		
		int visibility = lastDragItemPoint.typeId != null && lastDragItemPoint.typeId >= 4 ? View.GONE : View.VISIBLE; 
		tvPricePoint.setVisibility(visibility);
		lstPrice.setVisibility(visibility);
    	if (visibility == View.GONE) {
    		return;
    	}
		
		List<PriceTab> items = new ArrayList<PriceTab>();
		Object value = null;    	
		Long typeId = lastDragItemPoint.typeId == null ? 1 : lastDragItemPoint.typeId;
		if (typeId == 1) {
			if (lastDragItemPoint.id != null) {
				value = getValuePrice(lastDragItemPoint.id, 1);
			}
			items.add(new PriceTab(null, "Пропан", (long) 1, value));
		}
		else if (typeId == 2) {
			if (lastDragItemPoint.id != null) {
				value = getValuePrice(lastDragItemPoint.id, 2);
			}
	    	items.add(new PriceTab(null, "Метан", (long) 2, value));
		}
		else if (typeId == 3) {
			if (lastDragItemPoint.id != null) {
				value = getValuePrice(lastDragItemPoint.id, 1);
			}
			items.add(new PriceTab(null, "Пропан", (long) 1, value));
			if (lastDragItemPoint.id != null) {
				value = getValuePrice(lastDragItemPoint.id, 2);
			}
	    	items.add(new PriceTab(null, "Метан", (long) 2, value));
		}
		PriceTab[] results = new PriceTab[items.size()];
		items.toArray(results);
		
		lastDragItemPoint.prices = results;
		
		CustomPriceAdapter adapterPrice = new CustomPriceAdapter(mContext, items);
    	lstPrice.setAdapter(adapterPrice);
    	Utils.setListViewHeightBasedOnChildren(lstPrice);
	}
}
