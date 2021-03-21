package com.gasstation.calculator;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gasstation.R;
import com.gasstation.db.GSDbAdapter;

public class ArchiveFragment extends Fragment {

	private static final int DIALOG_PRICE = 100;
	private static final int DIALOG_FUELED = 101;
	private static final int DIALOG_DRIVED = 102;
	public static final String KEY_POSITION = "key_position";
	
	public static final String TAG = "ArchiveFragment";
	private TableLayout mTableLayout;
	private TextView mTextOneKmPrice;
	private TextView mTextSavedResult;
	private EditText mEditGazolinePrice;
	
	private LayoutInflater mInflater;
	
	private ArrayList<FuelItem> mList;
	private FuelItem mResultItem;
	
	private GSDbAdapter mAdapter;
	
    public static ArchiveFragment getInstance() {
        return new ArchiveFragment();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mInflater = inflater;
        View view = inflater.inflate(R.layout.fragment_archive, container, false);
        mTableLayout = (TableLayout) view.findViewById(R.id.archive_table);
        mTextOneKmPrice = (TextView) view.findViewById(R.id.archive_one_km_price);
        mTextSavedResult = (TextView) view.findViewById(R.id.archive_saved_result);
        mEditGazolinePrice = (EditText) view.findViewById(R.id.archive_gazoline_price);
        
        return view;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        
        mAdapter = new GSDbAdapter(getActivity());
        mAdapter.open();
        
        initGazolinePrice();
        getSavedFuelData();
        redrawTable();
    }
    
    private void initGazolinePrice() {
    	double price = mAdapter.getGazolinePrice();
    	if (price > 0.001) {
    		mEditGazolinePrice.setText(ToString.money(price));
    	}
        
        mEditGazolinePrice.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				writeAdditionlInfo();
				double gazolinePrice = toDouble(mEditGazolinePrice.getText().toString());
				mAdapter.updateGazolinePrice(gazolinePrice);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {	}
		});
	}

	private void redrawTable() {
		mResultItem = new FuelItem(mList);
		mTableLayout.removeAllViews();
		writeHeaders();
		writeResultInfo();
        writeFuelItems();
        writeAdditionlInfo();
	}

	@SuppressLint("InflateParams")
	private void writeHeaders() {
    	TableRow row = new TableRow(getActivity());

    	TextView view = (TextView) mInflater.inflate(R.layout.calc_table_header, null);
		view.setText(R.string.calc_date);
    	row.addView(view);
    	
    	view = (TextView) mInflater.inflate(R.layout.calc_table_header, null);
		view.setText(R.string.calc_drived);
    	row.addView(view);
    	
    	view = (TextView) mInflater.inflate(R.layout.calc_table_header, null);
		view.setText(R.string.calc_fueled);
    	row.addView(view);
    	
    	view = (TextView) mInflater.inflate(R.layout.calc_table_header, null);
		view.setText(R.string.calc_price);
    	row.addView(view);
    	
    	view = (TextView) mInflater.inflate(R.layout.calc_table_header, null);
		view.setText(R.string.calc_total);
    	row.addView(view);
    	
    	view = (TextView) mInflater.inflate(R.layout.calc_table_header, null);
		view.setText(R.string.calc_expense);
    	row.addView(view);
    	
		mTableLayout.addView(row);
	}
    
    private void writeFuelItems() {
    	for (int i = 0; i < mList.size(); i++) {
        	FuelItem item = mList.get(i);
        	TableRow row = new TableRow(getActivity());
        	
        	TextView view = createTextView(ToString.date(item.getDate()), false);
        	row.addView(view);
        	
        	view = createTextView(ToString.decimal(item.getDrived()), true);
        	view.setOnClickListener(createOnItemClick(DIALOG_DRIVED, i, getString(R.string.calc_drived), item.getDrived()));
        	row.addView(view);
        	
        	view = createTextView(ToString.decimal(item.getFueled()), true);
        	view.setOnClickListener(createOnItemClick(DIALOG_FUELED, i, getString(R.string.calc_fueled), item.getFueled()));
        	row.addView(view);
        	
        	view = createTextView(ToString.money(item.getPrice()), true);
        	view.setOnClickListener(createOnItemClick(DIALOG_PRICE, i, getString(R.string.calc_price), item.getPrice()));
        	row.addView(view);
        	
        	view = createTextView(ToString.money(item.getTotal()), false);
        	row.addView(view);
        	
        	view = createTextView(ToString.decimal(item.getExpense()), false);
        	row.addView(view);
        	
    		mTableLayout.addView(row);
        }
		
	}
    
    private OnClickListener createOnItemClick(final int requestCode, final int position, final String title, final double value) {
    	return new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putString(DoubleEditDialog.KEY_TITLE, title);
				args.putDouble(DoubleEditDialog.KEY_VALUE, value);
				args.putInt(KEY_POSITION, position);
				DoubleEditDialog dialog = DoubleEditDialog.newInstance(args);
				dialog.setTargetFragment(ArchiveFragment.this, requestCode);
				dialog.show(getFragmentManager(), "DoubleEditDialog");
			}
		};
	}
    
    @SuppressLint("InflateParams")
	private TextView createTextView(String value, boolean clickable) {
    	TextView view = (TextView) mInflater.inflate(R.layout.calc_table_cell, null);
		view.setText(value);
		view.setClickable(clickable);
		view.setBackgroundResource(clickable ? R.drawable.table_clickable_cell : R.drawable.table_cell_bg);
		return view;
    }

	@SuppressLint("InflateParams")
	private void writeResultInfo() {
		TableRow row = new TableRow(getActivity());
		
		TextView view = (TextView) mInflater.inflate(R.layout.calc_table_total, null);
		view.setText(R.string.calc_period);
    	row.addView(view);
    	
		view = (TextView) mInflater.inflate(R.layout.calc_table_total, null);
		view.setText(ToString.decimal(mResultItem.getDrived()));
		row.addView(view);
		
		view = (TextView) mInflater.inflate(R.layout.calc_table_total, null);
		view.setText(ToString.decimal(mResultItem.getFueled()));
		row.addView(view);
		
		view = (TextView) mInflater.inflate(R.layout.calc_table_total, null);
		view.setText(ToString.money(mResultItem.getPrice()));
		row.addView(view);
		
		view = (TextView) mInflater.inflate(R.layout.calc_table_total, null);
		view.setText(ToString.money(mResultItem.getTotal()));
		row.addView(view);
		
		view = (TextView) mInflater.inflate(R.layout.calc_table_total, null);
		view.setText(ToString.decimal(mResultItem.getExpense()));
		row.addView(view);
		
		mTableLayout.addView(row);
	}
    
    private void getSavedFuelData() {
    	mList = mAdapter.getAllFuelItems();
	}
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == Activity.RESULT_OK) {
    		Bundle args = data.getBundleExtra(DoubleEditDialog.KEY_BUNDLE);
    		if (args == null) {
    			return;
    		}
    		double value = args.getDouble(DoubleEditDialog.KEY_VALUE, -1);
    		int position = args.getInt(KEY_POSITION, -1);
    		if (position < 0 || position >= mList.size() || value < 0.001) {
    			return;
    		}
    	
    		FuelItem item = mList.get(position);
    		switch (requestCode) {
    			case DIALOG_PRICE:
    				item.setPrice(value);
    				break;
    			case DIALOG_FUELED:
    				item.setFueled(value);
    				break;
    			case DIALOG_DRIVED:
    				item.setDrived(value);
    				break;
    		}
    		mAdapter.updateFuelData(item);
    		redrawTable();
    		return;
    	}
    }
    
    private void writeAdditionlInfo() {
    	
    	double price = mResultItem.getOneKmPrice();
    	mTextOneKmPrice.setText(getString(R.string.archive_one_km_price, ToString.money(price)));
    	
    	double gazolinePrice = toDouble(mEditGazolinePrice.getText().toString());
    	double saved = 0.0;
    	if (gazolinePrice > 0.001) {
    		saved = mResultItem.getTotal() - (mResultItem.getFueled() * gazolinePrice);
    	}
    	mTextSavedResult.setText(getString(R.string.archive_saved_end, ToString.money(saved)));
	}
    
    private double toDouble(String text) {
    	try {
    		return Double.parseDouble(text);
    	} catch (NumberFormatException e) {
    		return 0.0;
    	}
    }
}