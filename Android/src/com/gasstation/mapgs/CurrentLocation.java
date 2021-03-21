package com.gasstation.mapgs;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class CurrentLocation {
	
	Context context;
	Location locationListen;
	
	protected LocationManager locationManager;
	
	public CurrentLocation(Context context) {
		this.context = context;
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
	}
	
	private final LocationListener mLocationListener = new LocationListener() {
	    @Override
	    public void onLocationChanged(final Location location) {
	    	locationListen = location;
	    }
	    @Override
	    public void onProviderDisabled(String provider) {
	    }
	    @Override
	    public void onProviderEnabled(String provider) {
	    }
	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	    }
	};
	
	public Location getLocation() {
		Location location = null;
        try {  
            // Getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                
                if (isNetworkEnabled) {
                    
                	if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);                        
                    }
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                	
                	if (locationManager != null) {
                        Location locGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);  
                        if (locGps != null) {
                        	location = locGps;
                        }
                    }
                }                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
}
