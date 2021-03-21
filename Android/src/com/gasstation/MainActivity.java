package com.gasstation;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.MapLayer;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.drag.DragAndDropItem;
import ru.yandex.yandexmapkit.overlay.location.MyLocationItem;
import ru.yandex.yandexmapkit.overlay.location.MyLocationOverlay;
import ru.yandex.yandexmapkit.overlay.location.OnMyLocationListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;
import Logging.Logger;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gasstation.calculator.CalculatorActivity;
import com.gasstation.common.AsyncTaskEx;
import com.gasstation.common.HttpRequest;
import com.gasstation.common.Settings;
import com.gasstation.common.Utils;
import com.gasstation.db.GSDbAdapter;
import com.gasstation.db.GSDbModelUpdater;
import com.gasstation.mapgs.BaseBalloonItem;
import com.gasstation.mapgs.CurrentLocation;
import com.gasstation.mapgs.DragAndDropOverlayEx;
import com.gasstation.mapgs.OverlayItemEx;
import com.gasstation.model.GPoint;
import com.gasstation.model.OilTypes;
import com.gasstation.model.PriceTab;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity implements OnMapListener, OnMyLocationListener {

	Menu menu;
	
	MapView mapView;
	MapController mMapController;
    OverlayManager mOverlayManager;
    Overlay mPointOverlay;
    DragAndDropOverlayEx mDragPoinOverlay;
    GSDbAdapter mDbAdapter;
    
    boolean isCreatePointMode = false;
    boolean isDragAndDropMode = false;
    
    GPoint lastDragItemPoint = null;   
    GPoint lastMapCenter = new GPoint();
    
    //ProgressDialog progressMyLocationDlg = null;
    private static boolean firstRun = true;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		InitErrorLog();
		
		mDbAdapter = new GSDbAdapter(this);
		mDbAdapter.open();
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.showBuiltInScreenButtons(true);
		
		mMapController = mapView.getMapController();

        mOverlayManager = mMapController.getOverlayManager();
        // Disable determining the user's location       
        //mMapController.getMapCenter()
        mOverlayManager.getMyLocation().addMyLocationListener(this);
        mOverlayManager.getMyLocation().setEnabled(true);        
        launchMyLoactionDialog();
        
        mMapController.addMapListener(this);        
        mMapController.showScaleView(true);
        mMapController.showZoomButtons(true);   
        mMapController.showJamsButton(false);        
        
        addPointsOnLayer();
        UpdatePointsAsyncTask atu = new UpdatePointsAsyncTask(this, true);
    	atu.execute();
        
        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
	}
	
	private void InitErrorLog() {
    	if (firstRun) { 

    		Logger.init(Environment.getExternalStorageDirectory(), getApplicationContext(), new Handler());
    		Thread.setDefaultUncaughtExceptionHandler(MMTException.inContext(this));
    		
    		firstRun = false;
    	}
    }
	
	public GSDbAdapter getDbAdapter() {
		return mDbAdapter;
	}
	
	public MapController getMapController() {
		return mMapController;
	}
	
	private void setScreenOrientation(boolean isFirstLoad) {
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
	}
	
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		lastDragItemPoint = new GPoint();
		if (savedInstanceState.containsKey("DragAndDropItem.LAT")) {
			
			lastDragItemPoint.lat = savedInstanceState.getDouble("DragAndDropItem.LAT"); 
			lastDragItemPoint.lng = savedInstanceState.getDouble("DragAndDropItem.LON");
		}
		if (savedInstanceState.containsKey("isCreatePointMode")) {
			isCreatePointMode = savedInstanceState.getBoolean("isCreatePointMode");
		}
		if (savedInstanceState.containsKey("isDragAndDropMode")) {
			isDragAndDropMode = savedInstanceState.getBoolean("isDragAndDropMode");
		}
		if (savedInstanceState.containsKey("DragAndDropItem.TITLE")) {
			lastDragItemPoint.title = savedInstanceState.getString("DragAndDropItem.TITLE");
		}
		if (savedInstanceState.containsKey("DragAndDropItem.ADDRESS")) {
			lastDragItemPoint.address = savedInstanceState.getString("DragAndDropItem.ADDRESS");
		}
		if (savedInstanceState.containsKey("DragAndDropItem.STATUS")) {
			lastDragItemPoint.statusId = savedInstanceState.getInt("DragAndDropItem.STATUS");
		}
		if (savedInstanceState.containsKey("DragAndDropItem.TYPE")) {
			lastDragItemPoint.typeId = savedInstanceState.getLong("DragAndDropItem.TYPE");
		}
		if (savedInstanceState.containsKey("DragAndDropItem.SCHEDULE")) {
			lastDragItemPoint.title = savedInstanceState.getString("DragAndDropItem.SCHEDULE");
		}
		if (savedInstanceState.containsKey("DragAndDropItem.ISBANKCARD")) {
			lastDragItemPoint.isBankCard = savedInstanceState.getInt("DragAndDropItem.ISBANKCARD");
		}
		if (savedInstanceState.containsKey("DragAndDropItem.PRICE")) {
			//lastDragItemPoint.price = savedInstanceState.getDouble("DragAndDropItem.PRICE");
		}
	    super.onRestoreInstanceState(savedInstanceState);	    
	}

	protected void onSaveInstanceState(Bundle outState) {
		
		if (isDragAndDropMode) {
			if (mDragPoinOverlay != null) {
				DragAndDropItem item = mDragPoinOverlay.getDragAndDropItem();
				if (item != null) {
	    			GeoPoint gp = item.getGeoPoint();
	    			outState.putDouble("DragAndDropItem.LAT", gp.getLat());
	    			outState.putDouble("DragAndDropItem.LON", gp.getLon());	    			
	    		}
				outState.putString("DragAndDropItem.GeoAddress", mDragPoinOverlay.GeoAddress);
			}
		}
		else if (isCreatePointMode) {
			if (lastDragItemPoint != null) {
				try {
					outState.putDouble("DragAndDropItem.LAT", lastDragItemPoint.lat);
	    			outState.putDouble("DragAndDropItem.LON", lastDragItemPoint.lng);
	    			outState.putString("DragAndDropItem.TITLE", lastDragItemPoint.title);
	    			outState.putString("DragAndDropItem.ADDRESS", lastDragItemPoint.address);
	    			outState.putInt("DragAndDropItem.STATUS", lastDragItemPoint.statusId);
	    			outState.putLong("DragAndDropItem.TYPE", lastDragItemPoint.typeId);
	    			outState.putString("DragAndDropItem.SCHEDULE", lastDragItemPoint.schedule);
	    			outState.putInt("DragAndDropItem.ISBANKCARD", lastDragItemPoint.isBankCard);
	    			//outState.putDouble("DragAndDropItem.PRICE", lastDragItemPoint.price);
				}
				catch(Exception e) {
					
				}
			}
		}
		outState.putBoolean("isCreatePointMode", isCreatePointMode);
		outState.putBoolean("isDragAndDropMode", isDragAndDropMode);
		
	    super.onSaveInstanceState(outState);	
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if (isCreatePointMode) {
        	showCreatePointDialog();
        }
	    else if (isDragAndDropMode) {
        	if (lastDragItemPoint != null) {
        		showDraggablePoint(new GeoPoint(lastDragItemPoint.lat, lastDragItemPoint.lng));
        	}
        	if (mDragPoinOverlay != null) {
        		mDragPoinOverlay.updateGeoCode();
        	}
        	setAddPointMode(true);
        }
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDbAdapter != null && isFinishing() == true) {
			mDbAdapter.close();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_action, menu);
        this.menu = menu;
        if (isDragAndDropMode) {        	
        	setAddPointMode(true);
        }
        return true;
    }
	
	public void showSelectPointInfo(GPoint point) {
		lastDragItemPoint = point;
		showCreatePointDialog();
	}
	
	public void launchMyLoactionDialog() {
		/*progressMyLocationDlg = ProgressDialog.show(MainActivity.this, 
				"Пожалуйста подождите ...", 
				"Идет определение Вашей геолокации ...", true);
		progressMyLocationDlg.setCancelable(true);*/
		Toast toast = Toast.makeText(this, "Идет определение Вашей геолокации ...", Toast.LENGTH_LONG); 
		toast.show();
	}

	
	private void showCreatePointDialog() {
		final CreateDialogForm dlgForm = new CreateDialogForm(lastDragItemPoint, this);
		final Dialog dlgCreatePoint = dlgForm.showCreatePointDialog();
		dlgCreatePoint.setOnDismissListener(new DialogInterface.OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				isCreatePointMode = false;
		    	isDragAndDropMode  = false;
			}
		});
		final Context currentContext = this;
		Button dlgBtnOk = (Button) dlgCreatePoint.findViewById(R.id.btn_point_create);                			
    	dlgBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				lastDragItemPoint = dlgForm.getEditPoint();
				if (lastDragItemPoint != null) {					
					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					Date date = new Date();
					
					/*if (lastDragItemPoint.id == null) {
						Integer count = mDbAdapter.fetchMax();
						count = count == null || count == -1 ? 0 : count;						
						mDbAdapter.createRow(count + 1, 
								lastDragItemPoint.lat, 
								lastDragItemPoint.lng, 
								lastDragItemPoint.title, 
								dateFormat.format(date),
								lastDragItemPoint.address,
								lastDragItemPoint.statusId,
								lastDragItemPoint.typeId,
								"",//lastDragItemPoint.schedule,
								lastDragItemPoint.isBankCard,
								lastDragItemPoint.prices,
								lastDragItemPoint.vote,
								lastDragItemPoint.rating
								);
						
					}
					else {
						mDbAdapter.updateRow(lastDragItemPoint.id, 
								lastDragItemPoint.lat, 
								lastDragItemPoint.lng, 
								lastDragItemPoint.title, 
								dateFormat.format(date),
								lastDragItemPoint.address,
								lastDragItemPoint.statusId,
								lastDragItemPoint.typeId,
								"",//lastDragItemPoint.schedule,
								lastDragItemPoint.isBankCard,
								lastDragItemPoint.prices,
								lastDragItemPoint.vote,
								lastDragItemPoint.rating);
					}*/
					AddPointAsyncTask atp = new AddPointAsyncTask(currentContext, false);
	        		atp.execute(lastDragItemPoint);
	        		lastDragItemPoint = null;
					
					dlgCreatePoint.dismiss();
					//addPointsOnLayer();
				}
			}
		});
		Button dlgBtnCancel = (Button) dlgCreatePoint.findViewById(R.id.btn_point_cancel);                			
		dlgBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lastDragItemPoint = null;
				dlgCreatePoint.dismiss();
			}
		});                 
		dlgCreatePoint.show();
	}
	
	public GeoPoint getMyLocation() {
		GeoPoint point = null;
    	if (mOverlayManager.getMyLocation().getMyLocationItem() != null) {
    		point = mOverlayManager.getMyLocation().getMyLocationItem().getGeoPoint();
    	}
    	return point;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
		    case R.id.itm_contacts:
		    case R.id.itm_help:
			    {
			    	final Dialog dlg = new Dialog(this);
			    	dlg.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
			    	if (item.getItemId() == R.id.itm_contacts) {
			    		dlg.setContentView(R.layout.contact_layout);
			    	}
			    	else {
			    		dlg.setContentView(R.layout.help_layout);
			    	}
	            	dlg.show();
			    }
		    	return true;
		    case R.id.itm_apply:
		    case R.id.itm_cancel:
		    	
		    	if (item.getItemId() == R.id.itm_apply) {
		    		if (lastDragItemPoint == null) {
		    			lastDragItemPoint = new GPoint();
		    		}
		    		if (mDragPoinOverlay != null) {
						DragAndDropItem itemDrag = mDragPoinOverlay.getDragAndDropItem();
						if (item != null) {
			    			GeoPoint gp = itemDrag.getGeoPoint();
			    			lastDragItemPoint.lat = gp.getLat();
			    			lastDragItemPoint.lng = gp.getLon();	    			
			    		}
						lastDragItemPoint.address = mDragPoinOverlay.GeoAddress;
					}
		    		showCreatePointDialog();
		    	}
		    	
		    	isCreatePointMode = item.getItemId() == R.id.itm_apply;
		    	isDragAndDropMode  = false;
		    	
		    	removeDraggableOverlay();
		    	
		    	setAddPointMode(false);
		    	return true;
	        case R.id.itm_add_point:	        	
	        	
	        	GeoPoint point = getMyLocation();	        	
	        	if (point == null) {
		        	CurrentLocation curLoc = new CurrentLocation(this);
		        	Location location = curLoc.getLocation();	     
		        	if (location != null) {
		        		point = new GeoPoint(location.getLatitude(), location.getLongitude());
		        	}
	        	}
	        	if (point != null) {
		        	//AddPointAsyncTask atp = new AddPointAsyncTask(this);
	        		//atp.execute(point);
	        		showDraggablePoint(point);
        	    	setAddPointMode(true);
        	    	if (mDragPoinOverlay != null) {
                		mDragPoinOverlay.updateGeoCode();
                	}
	        	}
	        	else {
	        		Toast toast = Toast.makeText(this, getString(R.string.msg_not_current_location), Toast.LENGTH_LONG); 
	    			toast.show();
	        	}
        		
	            return true;
	        case R.id.itm_upd_points:
	        	
	        	UpdatePointsAsyncTask atu = new UpdatePointsAsyncTask(this, false);
	        	atu.execute();
	        	
	            return true;
	        case R.id.itm_settings:
		        {
		        	final Dialog dlg = new Dialog(this);
	            	dlg.setContentView(R.layout.settings_layout);
	            	dlg.setTitle(getString(R.string.settings));
	            	
	            	final EditText textTitle = (EditText) dlg.findViewById(R.id.et_ip);
	            	
	            	final Settings settings = new Settings(this);            	
	            	textTitle.setText(settings.getString(Settings.IP_ADDRESS, "213.141.148.66"));
	            	
	            	final Spinner spType = (Spinner) dlg.findViewById(R.id.sp_type_point);
	            	ArrayList<String> oilTypes = OilTypes.GetStrOilTypes(true);
	            	String[] values = new String[oilTypes.size()];
	            	oilTypes.toArray(values);
	            	for(int i = 0; i < values.length; i++) {
	            		if (values[i] == "Пропан+Метан") {
	            			values[i] = "Все";
	            		}
	            	}
	            	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values );
	            	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	            	spType.setAdapter(adapter);
	            	
	            	final String value = settings.getString(Settings.CURRENT_SELECTED_GAZ, String.valueOf(OilTypes.defaultType));
	            	spType.setSelection(OilTypes.GetIndexFromTypeId(Long.valueOf(value)));
	            	
	            	Button dlgBtnOk = (Button) dlg.findViewById(R.id.btn_settings_ok);                			
	            	dlgBtnOk.setOnClickListener(new OnClickListener() {
	    				@Override
	    				public void onClick(View v) {    					
	    					settings.putString(Settings.IP_ADDRESS, textTitle.getText().toString());
	    					String saveValue = String.valueOf(OilTypes.GetTypeIdFromIndex(spType.getSelectedItemPosition()));
	    					if (saveValue == String.valueOf(OilTypes.defaultType)) {
	    						saveValue = null;
	    					}
	    					settings.putString(Settings.CURRENT_SELECTED_GAZ, saveValue);
	    					if (value != saveValue) {
	    						addPointsOnLayer();
	    					}
	    					
	    					dlg.dismiss();    					
	    				}
	    			});
	    			Button dlgBtnCancel = (Button) dlg.findViewById(R.id.btn_settings_cancel);                			
	    			dlgBtnCancel.setOnClickListener(new OnClickListener() {
	    				@Override
	    				public void onClick(View v) {
	    					dlg.dismiss();
	    				}
	    			});                 
	    			dlg.show();
		        }
	        	return true;
	        	
	        case R.id.itm_calculator:
	        	Intent calculatorActivity = new Intent(this, CalculatorActivity.class);
                startActivity(calculatorActivity);
	        	
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	class UpdatePointsAsyncTask extends AsyncTaskEx {

		public UpdatePointsAsyncTask(Context context, boolean isHidden) {
			super(context, isHidden);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected Object doInBackground(Object... params) {
			String result = null;
			try {
				//хранить stamp в пропертях, и обновлять данные после каждой передачи, успешной
				//stamp = время последней синхры, должно быть UTC
				Settings settings = new Settings(context);            	
            	String ip = settings.getString(Settings.IP_ADDRESS, "213.141.148.66");
            	String time = settings.getString(Settings.TIME_UPDATE, "");
            	
				HttpRequest request = new HttpRequest("http://" + ip + "/gazstation/points/get?stamp=" + time);
				result = request.requestGet();
			} catch (Exception e) {			
				// TODO Auto-generated catch block
				e.printStackTrace();
				exception = e;
			}
			return result;
		}
		
		@Override
	    protected void onPostExecute(Object result) {
			
			if (result != null) {
				try {
					org.json.JSONArray array = new org.json.JSONArray(result.toString());
		        	for(int i = 0; i < array.length(); i++) {
		        		JSONObject jsonObj = (JSONObject)array.get(i);		        		
		        		
		        		GPoint point = new GPoint();
						point.id = jsonObj.getLong("id");
						point.address = jsonObj.getString("address");
						point.title = jsonObj.getString("name");
						point.typeId = jsonObj.getLong("type");
						point.statusId = jsonObj.getInt("state");
						point.schedule = jsonObj.getString("worktime");
						point.lat = jsonObj.isNull("latitude") ? null : jsonObj.getDouble("latitude");
						point.lng = jsonObj.isNull("longitude") ? null : jsonObj.getDouble("longitude");
						point.isBankCard = jsonObj.getBoolean("cardAccepted") ? 1 : 0;		        		
		        		
		        		point.voteCount = jsonObj.getInt("voteCount");
		        		point.rating = jsonObj.getDouble("rating");
		        		
		        		Boolean isDeleted = jsonObj.getBoolean("deleted");
		        				        		
		        		org.json.JSONArray arrayPrices = jsonObj.getJSONArray("prices");
		        		if (arrayPrices != null) {
		        			List<PriceTab> items = new ArrayList<PriceTab>();
			        		for(int p = 0; p < arrayPrices.length(); p++) {
			        			
			        			JSONObject jsonPrice = (JSONObject)arrayPrices.get(p);
			        			Long type = jsonPrice.getLong("type");
			        			Double price = jsonPrice.getDouble("price");
			        			Integer date = jsonPrice.optInt("date");
			        			DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
			        			Date currentDate = Utils.getCurrentDate();
			        			
			        			PriceTab priceTab = new PriceTab(null, null, type, price, dateFormat.format(currentDate));
			        			items.add(priceTab);			        			
			        		}
			        		PriceTab[] results = new PriceTab[items.size()];
		        			items.toArray(results);
		        			point.prices = results;
		        		}
		        		
		        		Cursor o = mDbAdapter.fetchTodo(point.id);
		        		GSDbModelUpdater db = new GSDbModelUpdater(context);
						db.Update(point, o.getCount() == 0 ? GSDbModelUpdater.TypeUpdate.Insert : 
							isDeleted ?GSDbModelUpdater.TypeUpdate.Delete : GSDbModelUpdater.TypeUpdate.Update);
		        	}
		        	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exception = e;
				}				
			}		
			if (exception == null) {
				Settings settings = new Settings(context);
				settings.putString(Settings.TIME_UPDATE, String.valueOf(System.currentTimeMillis() / 1000));
				addPointsOnLayer();
			}
			
	    	super.onPostExecute(result);
		}
	}
	
	class AddPointAsyncTask extends AsyncTaskEx {

		public AddPointAsyncTask(Context context, boolean isHidden) {
			super(context, isHidden);
			// TODO Auto-generated constructor stub
		}
		
		private String arrayValues(PriceTab[] priceTab) {
			String values = "";
			if (priceTab != null)
				for (int i = 0; i < priceTab.length; i++) {
					values += "{'type': " + priceTab[i].TypeId + ",'price':" + priceTab[i].Value + "}" + (i < priceTab.length - 1 ? "," : "");
				}
			return values.trim();
		}
		
		@Override
		protected Object doInBackground(Object... params) {
			String result = null;
			try {
				Settings settings = new Settings(context);            	
            	String ip = settings.getString(Settings.IP_ADDRESS, "213.141.148.66");
            	
				HttpRequest request = new HttpRequest("http://" + ip + "/gazstation/points/add");
				result = request.requestPost(
						"{ 'id' : " + ((GPoint)params[0]).id +
						", 'latitude' : " + ((GPoint)params[0]).lat + 
						", 'longitude' : " + ((GPoint)params[0]).lng + 
						", 'isCardAccepted' : " + ((GPoint)params[0]).isBankCard + 
						", 'address' : '" + ((GPoint)params[0]).address + "'" +
						", 'state' : " + ((GPoint)params[0]).statusId + 
						", 'type' : " + ((GPoint)params[0]).typeId + 
						", 'name' : '" + ((GPoint)params[0]).title + "'" +
						", 'worktime' : '" + ((GPoint)params[0]).schedule + "'" +
						", 'prices' : [" + arrayValues(((GPoint)params[0]).prices) + "]" +
						" }"
						
						);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				exception = e;
			} 
			
			return result;
		}
		
		@Override
	    protected void onPostExecute(Object result) {
				
			if (result != null) {
				try {
					org.json.JSONObject jsonObj = new org.json.JSONObject(result.toString());					
					
					GPoint point = new GPoint();
					point.id = jsonObj.getLong("id");
					point.address = jsonObj.getString("address");
					point.title = jsonObj.getString("name");
					point.typeId = jsonObj.getLong("type");
					point.statusId = jsonObj.getInt("state");
					point.schedule = jsonObj.getString("worktime");
					point.lat = jsonObj.isNull("latitude") ? null : jsonObj.getDouble("latitude");
					point.lng = jsonObj.isNull("longitude") ? null : jsonObj.getDouble("longitude");
					point.isBankCard = jsonObj.getBoolean("cardAccepted") ? 1 : 0;
					
					point.voteCount = jsonObj.getInt("voteCount");
					point.rating = jsonObj.getDouble("rating");
					
	        		Boolean isDeleted = jsonObj.getBoolean("deleted");
	        		
	        		org.json.JSONArray arrayPrices = jsonObj.getJSONArray("prices");
	        		if (arrayPrices != null) {
	        			List<PriceTab> items = new ArrayList<PriceTab>();
		        		for(int p = 0; p < arrayPrices.length(); p++) {
		        			
		        			JSONObject jsonPrice = (JSONObject)arrayPrices.get(p);
		        			Long type = jsonPrice.getLong("type");
		        			Double price = jsonPrice.getDouble("price");
		        			Integer date = jsonPrice.optInt("date");//0 если null
		        			DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
		        			Date currentDate = Utils.getCurrentDate();
		        			
		        			PriceTab priceTab = new PriceTab(null, null, type, price, dateFormat.format(currentDate));
		        			items.add(priceTab);		        			
		        		}
		        		PriceTab[] results = new PriceTab[items.size()];
	        			items.toArray(results);
	        			point.prices = results;
	        		}
	        		
	        		Cursor o = mDbAdapter.fetchTodo(point.id);
	        		GSDbModelUpdater db = new GSDbModelUpdater(context);
					db.Update(point, o.getCount() == 0 ? GSDbModelUpdater.TypeUpdate.Insert : GSDbModelUpdater.TypeUpdate.Update);
	        		
        			//showPoint(mPointOverlay, point);            			
        			//mMapController.setPositionAnimationTo(new GeoPoint(point.lat, point.lng));
	        		addPointsOnLayer();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exception = e;
				} 
			}			
	    	super.onPostExecute(result);
		}
	}
	
	private void removeDraggableOverlay() {
		if (mDragPoinOverlay != null) {
			mDragPoinOverlay.clearOverlayItems();
			mOverlayManager.removeOverlay(mDragPoinOverlay);
		}
	}
	private void showDraggablePoint(GeoPoint point) {
		removeDraggableOverlay();
		mDragPoinOverlay = new DragAndDropOverlayEx(mMapController);		
		
		float density = getResources().getDisplayMetrics().density;
        int offsetX = (int)(-7 * density);
        int offsetY = (int)(20 * density);
        
		Resources res = getResources();
		DragAndDropItem dragItem = new DragAndDropItem(point, res.getDrawable(R.drawable.drag_marker));
		dragItem.setOffsetX(offsetX);
		dragItem.setOffsetY(offsetY);
		BalloonItem balloonDrag = new BalloonItem(this, dragItem.getGeoPoint());
		balloonDrag.setText("нет данных");       
		balloonDrag.setOffsetX(offsetX);
        dragItem.setBalloonItem(balloonDrag);		
		dragItem.setDragable(true);
		
		mDragPoinOverlay.addOverlayItem(dragItem);
		mOverlayManager.addOverlay(mDragPoinOverlay);
		
		isDragAndDropMode = true;		

		mMapController.setZoomCurrent(17);
		mMapController.setPositionAnimationTo(point);
	}
	private Drawable getDrawable(GPoint point) {
		Resources res = getResources();		
		Drawable drw = null;
		switch(point.typeId.intValue()) {
		case 1:
			drw = res.getDrawable(mMapController.getZoomCurrent() < 10 ? R.drawable.sblue_point : R.drawable.blue_point);
			break;
		case 2:
			drw = res.getDrawable(mMapController.getZoomCurrent() < 10 ? R.drawable.sred_point : R.drawable.red_point);
			break;
		case 3:
			drw = res.getDrawable(mMapController.getZoomCurrent() < 10 ? R.drawable.sredblue_point : R.drawable.redblue_point);
			break;
		case 4:
			drw = res.getDrawable(mMapController.getZoomCurrent() < 10 ? R.drawable.sservice : R.drawable.service);
			break;
		}
		if (point.statusId == 2) {
			drw = res.getDrawable(mMapController.getZoomCurrent() < 10 ? R.drawable.sdisable_gazstation : R.drawable.disable_gazstation);
		}
		/*if (drw != null) {
			drw.setAlpha(point.statusId == 2 ? 70 : 255);
		}*/
		return drw;
	}
	private void showPoint(Overlay overlay, GPoint point) {
			
		Drawable drw = getDrawable(point);		
		if (drw != null) {
			
			OverlayItemEx item = new OverlayItemEx(new GeoPoint(point.lat, point.lng), drw, point);
			item.setOffsetY(23);
	        BaseBalloonItem balloon = new BaseBalloonItem(this, item.getGeoPoint(), point);      
	        balloon.setBalloonContent();
	        balloon.setOnViewClickListener();	        
			item.setBalloonItem(balloon);
	        overlay.addOverlayItem(item); 
		}
	}
	
	public void addPointsOnLayer() {
		if (mPointOverlay == null) {
			mPointOverlay = new Overlay(mMapController);
		}
		else {
			mPointOverlay.clearOverlayItems();
			mOverlayManager.removeOverlay(mPointOverlay);
		}		
		
		final Settings settings = new Settings(this);
		String value = settings.getString(Settings.CURRENT_SELECTED_GAZ, null);
		
		Cursor cResult = value == null ? mDbAdapter.fetchAllRows() : 
			mDbAdapter.fetchTodo(GSDbAdapter.KEY_TYPE + " = " + value + " OR " + GSDbAdapter.KEY_TYPE + " = 4" + " OR " + GSDbAdapter.KEY_TYPE + " = 3");
		cResult.moveToFirst();
		while(!cResult.isAfterLast()) {
			GPoint point = new GPoint();
			point.id = cResult.getLong(cResult.getColumnIndex(GSDbAdapter.KEY_ROWID));
			point.lat = cResult.getDouble(cResult.getColumnIndex(GSDbAdapter.KEY_LAT));
			point.lng = cResult.getDouble(cResult.getColumnIndex(GSDbAdapter.KEY_LNG));
			
			point.title = cResult.isNull(cResult.getColumnIndex(GSDbAdapter.KEY_TITLE)) ? null : 
				cResult.getString(cResult.getColumnIndex(GSDbAdapter.KEY_TITLE)).equals("null") ? "" : cResult.getString(cResult.getColumnIndex(GSDbAdapter.KEY_TITLE));
			
			point.address = cResult.isNull(cResult.getColumnIndex(GSDbAdapter.KEY_ADDRESS)) ? null : 
				cResult.getString(cResult.getColumnIndex(GSDbAdapter.KEY_ADDRESS)).equals("null") ? "" : cResult.getString(cResult.getColumnIndex(GSDbAdapter.KEY_ADDRESS));
			
			point.statusId = cResult.getInt(cResult.getColumnIndex(GSDbAdapter.KEY_STATUS));
			point.typeId = cResult.getLong(cResult.getColumnIndex(GSDbAdapter.KEY_TYPE));
			
			point.schedule = cResult.isNull(cResult.getColumnIndex(GSDbAdapter.KEY_SCHEDULE)) ? null : 
				cResult.getString(cResult.getColumnIndex(GSDbAdapter.KEY_SCHEDULE)).equals("null") ? "" : cResult.getString(cResult.getColumnIndex(GSDbAdapter.KEY_SCHEDULE));
			
			point.isBankCard = cResult.getInt(cResult.getColumnIndex(GSDbAdapter.KEY_ISBANKCARD));
			
			point.vote = cResult.getInt(cResult.getColumnIndex(GSDbAdapter.KEY_VOTE));
			point.rating = cResult.getDouble(cResult.getColumnIndex(GSDbAdapter.KEY_RATING));
			point.voteCount = cResult.getInt(cResult.getColumnIndex(GSDbAdapter.KEY_VOTE_COUNT));
			//point.date = Date.parse(cResult.getString(cResult.getColumnIndex(GSDbAdapter.KEY_DATE)));
			
			showPoint(mPointOverlay, point);
            
			cResult.moveToNext();
		}
		
		mOverlayManager.addOverlay(mPointOverlay);
		mMapController.notifyRepaint();
	}
	
	private void setAddPointMode(boolean isEdit) {
		
		int layerId = 1;
		if (isEdit) {
			layerId = 2;
		}
		if (this.menu != null) {
			this.menu.setGroupVisible(R.id.itm_point_group, isEdit);
			this.menu.setGroupVisible(R.id.itm_main_group, !isEdit);
		}
		
		List<MapLayer> list = mMapController.getListMapLayer();
    	for(MapLayer mapLayer : list) {
    		if (mapLayer.layerId == layerId) {
    			mMapController.setCurrentMapLayer(mapLayer);
    		}
    	}    	
	}
	
	@Override
    public void onMapActionEvent(MapEvent event) {
		
		switch (event.getMsg()) {
		case MapEvent.MSG_SCALE_END:
		case MapEvent.MSG_ZOOM_END:
			for(Object ovItem : mPointOverlay.getOverlayItems()) {
				if (ovItem instanceof OverlayItemEx) {
					Drawable drw = getDrawable(((OverlayItemEx)ovItem).getGPoint());
					((OverlayItemEx)ovItem).setDrawable(drw);
				}
			}
			break;
        case MapEvent.MSG_LONG_PRESS:
            
        	final GeoPoint point = mMapController.getGeoPoint(new ScreenPoint(event.getX(), event.getY())); 
        	
        	MainActivity.this.runOnUiThread(new Runnable() {
        	    public void run() {
        	    	showDraggablePoint(point);
        	    	setAddPointMode(true);
        	    }
        	});        	
        	
            break;
		}
	}

	@Override
    public void onMyLocationChange(MyLocationItem myLocationItem) {
		//if (progressMyLocationDlg != null) progressMyLocationDlg.dismiss();
		
	}
}
