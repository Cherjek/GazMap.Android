package com.gasstation.calculator;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gasstation.R;
import com.gasstation.db.GSDbAdapter;

public class CalculatorFragment extends Fragment {

	public static final String TAG = "CalculatorFragment";
	
	private EditText mEditFueled;
	private EditText mEditPrice;
    private EditText mEditDrived;
    private TextView mViewTotal;
    private TextView mViewExpense;
    
    private GSDbAdapter mAdapter;
    
	TextWatcher mTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			recalcTotal();
	        recalExpense();
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	
    public static CalculatorFragment getInstance() {
        return new CalculatorFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);

        mEditFueled = (EditText)view.findViewById(R.id.fueled);
        mEditPrice = (EditText)view.findViewById(R.id.price);
        mEditDrived = (EditText)view.findViewById(R.id.drived);
        
        mViewTotal = (TextView)view.findViewById(R.id.total);
        mViewExpense = (TextView)view.findViewById(R.id.expense);
        
        mEditFueled.addTextChangedListener(mTextWatcher);
        mEditPrice.addTextChangedListener(mTextWatcher);
        mEditDrived.addTextChangedListener(mTextWatcher);
        
        Button button = (Button)view.findViewById(R.id.btn_save);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (saveData()) {
					getActivity().finish();
				} else {
					Toast.makeText(getActivity(), getString(R.string.calc_save_error), Toast.LENGTH_LONG).show();
				}
			}
		});
        
        button = (Button)view.findViewById(R.id.btn_archive);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gotoArchive();
			}
		});

        
        return view;
    }
    
    private boolean saveData() {
    	double fueled = toDouble(mEditFueled.getText().toString());
    	double price = toDouble(mEditPrice.getText().toString());
    	double drived = toDouble(mEditDrived.getText().toString());
    	
    	boolean isOk = true;
    	if (fueled < 0.001) {
    		mEditFueled.setError(getString(R.string.calc_field_error));
    		isOk = false;
    	}
    	if (price < 0.001) {
    		mEditPrice.setError(getString(R.string.calc_field_error));
    		isOk = false;
    	}
    	if (drived < 0.001) {
    		mEditDrived.setError(getString(R.string.calc_field_error));
    		isOk = false;
    	}
    	
    	if (isOk) {
    		mAdapter.insertFuelData(fueled, price, drived);
    	}
    	return isOk;
	}

	private void recalcTotal() {
    	double fueled = toDouble(mEditFueled.getText().toString());
    	double price = toDouble(mEditPrice.getText().toString());
    	double total = fueled * price;
    	mViewTotal.setText(ToString.money(total));
    }
    
    private void recalExpense() {
    	double fueled = toDouble(mEditFueled.getText().toString());
    	double drived = toDouble(mEditDrived.getText().toString());
    	
    	if (drived < 0.001) {
    		mViewExpense.setText("0.0");
    	} else {
    		double expense = (fueled / drived) * 100.0;
    		mViewExpense.setText(ToString.decimal(expense));
    	}
    }
    
    private double toDouble(String text) {
    	try {
    		return Double.parseDouble(text);
    	} catch (NumberFormatException e) {
    		return 0.0;
    	}
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        
        mAdapter = new GSDbAdapter(getActivity());
        mAdapter.open();
        double price = mAdapter.getLastFueledPrice();
        if (price > 0.001) {
        	mEditPrice.setText(ToString.money(price));
        }
		
        recalcTotal();
        recalExpense();
    }
    
    private void gotoArchive() {
    	ArchiveFragment archiveFragment= ArchiveFragment.getInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, archiveFragment, ArchiveFragment.TAG);
        transaction.addToBackStack(ArchiveFragment.TAG);
        transaction.commit();
    }
}
