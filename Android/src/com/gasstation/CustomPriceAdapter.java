package com.gasstation;

import java.util.List;

import com.gasstation.common.IListViewAdapter;
import com.gasstation.common.Utils;
import com.gasstation.model.PriceTab;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class CustomPriceAdapter extends ArrayAdapter<PriceTab> implements IListViewAdapter {
	
	public boolean stopUpdate = false;
	private final static int resourceId = R.layout.lst_priceview_item;
	public CustomPriceAdapter(Context context, List<PriceTab> objects) {
		super(context, resourceId, objects);		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		PriceTab item = getItem(position);
		final int positionEx = position;
		if (convertView == null) {			
			convertView = View.inflate(getContext(), resourceId, null);			
		}
		((TextView) convertView.findViewById(R.id.tv_price_type_point))
				.setText(item.Name);
		
		if (item.Value != null && !stopUpdate) {
    		
			((EditText) convertView.findViewById(R.id.et_price_type_point)).setText(
					Utils.getDecimalValueFormat("#####0.00", item.Value));			
    	}		
		((EditText) convertView.findViewById(R.id.et_price_type_point)).addTextChangedListener(new TextWatcher() {

    	    public void onTextChanged(CharSequence s, int start, int before,
    	            int count) {
    	    	PriceTab itemEx = getItem(positionEx);
    	    	if (s.length() > 0) {    	    		
    	    		itemEx.Value = Double.parseDouble(String.format("%s", s).replace(',', '.'));
    	    	}
    	    	else {
    	    		itemEx.Value = 0.0;
    	    	}
    	    }

    	    public void beforeTextChanged(CharSequence s, int start, int count,
    	            int after) {

    	    }

    	    public void afterTextChanged(Editable s) {

    	    }
    	});
		
		return convertView;
	}

	@Override
	public void stopUpdate(boolean stopUpdate) {
		this.stopUpdate = stopUpdate;
	}
}
