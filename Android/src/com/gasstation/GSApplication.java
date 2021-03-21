package com.gasstation;

public class GSApplication extends android.app.Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		//ACRA.init(this); 
        //ErrorReporter.getInstance().checkReportsOnApplicationStart();
	}
}
