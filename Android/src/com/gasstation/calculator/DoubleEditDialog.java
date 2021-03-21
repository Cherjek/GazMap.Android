package com.gasstation.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;

import com.gasstation.R;

public class DoubleEditDialog extends DialogFragment {

	public static final String KEY_BUNDLE = "KEY_BUNDLE";
	public static final String KEY_TITLE = "KEY_TITLE";
	public static final String KEY_VALUE = "KEY_VALUE";
	
	private String _title;
	private Double _value;
	
	public static DoubleEditDialog newInstance (Bundle args) {
		DoubleEditDialog frag = new DoubleEditDialog();
        frag.setArguments(args);
        return frag;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
    	Bundle args = getArguments();
    	parseArguments(args);
        
        Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(_title);
        
        EditText view = new EditText (getActivity());
		view.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		
		view.setText(getValue());
		view.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				setValue(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		
		dialog.setView(view);
		dialog.setPositiveButton(R.string.btn_ok, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult(Activity.RESULT_OK);
			}
		});
		
		dialog.setNegativeButton(R.string.btn_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				sendResult(Activity.RESULT_CANCELED);
			}
		});
        
        return dialog.create();
    }
    
    private String getValue() {
    	return _value == null || _value == 0.0 ? ToString.EMPTY : String.valueOf(_value);
	}
    
	protected void setValue(String value) {
		try {
			_value = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			_value = null;
		}
	}

	protected void parseArguments(Bundle args) {
		_title = args.getString(KEY_TITLE, ToString.EMPTY);
		_value = args.getDouble(KEY_VALUE, 0.0);
	}
	
	protected void sendResult (int resultCode) {
		if (getTargetFragment() == null) {
			return;
		}
		Intent intent = new Intent();
		Bundle args = getArguments();
		
		args.remove(KEY_VALUE);
		if (_value != null) {
			args.putDouble(KEY_VALUE, _value);
		}
		intent.putExtra(KEY_BUNDLE, args);
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
	}
}	