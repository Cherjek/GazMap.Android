package com.gasstation.common;

import com.gasstation.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.widget.Toast;

public class AsyncTaskEx extends AsyncTask<Object, Void, Object> {

	ProgressDialog dialog;
	public Context context;
	public Exception exception = null;
	public boolean isHidden;
	
	public AsyncTaskEx(Context context, boolean isHidden) {
		this.context = context;
		this.isHidden = isHidden;
	}
	
	@Override
    protected void onPreExecute() {
    	super.onPreExecute();
    	
    	dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage(context.getString(R.string.msg_please_wait));
        dialog.setOnKeyListener(new OnKeyListener() {                   
            
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
        if (!isHidden) dialog.show();
	}
	
	@Override
    protected Object doInBackground(Object... params) {
    	
    	return null;
    }
	
	@Override
    protected void onPostExecute(Object result) {
    	super.onPostExecute(result);
    	
    	dialog.dismiss();
    	
    	if (exception != null) {
    		String error_text = exception.getMessage();
    		if (exception instanceof org.apache.http.client.ClientProtocolException) {
    			error_text = context.getString(R.string.msg_error_connect);	    		
    		}
    		else if (exception instanceof org.apache.http.conn.HttpHostConnectException) {
    			error_text = context.getString(R.string.msg_host_not_connect);
    		}
    		
    		Toast toast = Toast.makeText(context, error_text, Toast.LENGTH_LONG); 
			toast.show();
    	}
	}
}
