package com.gasstation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gasstation.db.GSDbAdapter;
import com.gasstation.mapgs.BaseBalloonItem;
import com.gasstation.model.GPoint;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.MapView;
import ru.yandex.yandexmapkit.OverlayManager;
import ru.yandex.yandexmapkit.map.MapEvent;
import ru.yandex.yandexmapkit.map.OnMapListener;
import ru.yandex.yandexmapkit.overlay.Overlay;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;
import ru.yandex.yandexmapkit.utils.ScreenPoint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnMapListener {

	MapController mMapController;
    OverlayManager mOverlayManager;    
    OverlayItem mEmptyOverlayItem;
    
    GSDbAdapter mDbAdapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDbAdapter = new GSDbAdapter(this);
		mDbAdapter.open();
		
		final MapView mapView = (MapView) findViewById(R.id.map);
		mapView.showBuiltInScreenButtons(true);
		
		mMapController = mapView.getMapController();

        mOverlayManager = mMapController.getOverlayManager();
        // Disable determining the user's location
        mOverlayManager.getMyLocation().setEnabled(true);
        
        // add listener
        mMapController.addMapListener(this);        
        mMapController.showScaleView(true);
        mMapController.showZoomButtons(true);
        
        addPointsOnLayer();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mDbAdapter != null) {
			mDbAdapter.close();
		}
	}
	
	private void showPoint(Overlay overlay, GPoint point) {
		Resources res = getResources();				
		OverlayItem item = new OverlayItem(new GeoPoint(point.lat, point.lng), res.getDrawable(R.drawable.marker));
		
		// Create a balloon model for the object
        BaseBalloonItem balloon = new BaseBalloonItem(this, item.getGeoPoint(), point);

        balloon.setOnViewClickListener();
        // Add the balloon model to the object
        item.setBalloonItem(balloon);
        // Add the object to the layer
        overlay.addOverlayItem(item);   
        
	}
	
	public void addPointsOnLayer() {
		Overlay overlay = new Overlay(mMapController);
		
		Cursor cResult = mDbAdapter.fetchAllRows();
		cResult.moveToFirst();
		while(!cResult.isAfterLast()) {
			GPoint point = new GPoint();
			point.id = cResult.getInt(cResult.getColumnIndex(GSDbAdapter.KEY_ROWID));
			point.lat = cResult.getDouble(cResult.getColumnIndex(GSDbAdapter.KEY_LAT));
			point.lng = cResult.getDouble(cResult.getColumnIndex(GSDbAdapter.KEY_LNG));
			point.title = cResult.getString(cResult.getColumnIndex(GSDbAdapter.KEY_TITLE));
			//point.date = Date.parse(cResult.getString(cResult.getColumnIndex(GSDbAdapter.KEY_DATE)));
			
			showPoint(overlay, point);
			cResult.moveToNext();
		}
		
		mOverlayManager.getOverlays().clear();
		mOverlayManager.addOverlay(overlay);
	}
	
	@Override
    public void onMapActionEvent(MapEvent event) {
		
		switch (event.getMsg()) {
        case MapEvent.MSG_LONG_PRESS:
            
        	/*final GeoPoint point = mMapController.getGeoPoint(new ScreenPoint(event.getX(), event.getY()));
        	
        	final CharSequence[] items = {
                    "Добавить точку"
            };

        	MainActivity.this.runOnUiThread(new Runnable() {
        	    public void run() {
        	    	AlertDialog.Builder builder = new AlertDialog.Builder(mMapController.getContext());
                    builder.setTitle("");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                        	
                        	final Dialog dlg = new Dialog(mMapController.getContext());
                        	dlg.setContentView(R.layout.create_item_layout);
                        	dlg.setTitle("Добавление точки");
                        	
                        	final EditText textTitle = (EditText) dlg.findViewById(R.id.et_title_point);
                        	textTitle.setText("Не задан");
                        	Button dlgBtnOk = (Button) dlg.findViewById(R.id.btn_point_create);                			
                        	dlgBtnOk.setOnClickListener(new OnClickListener() {
                				@Override
                				public void onClick(View v) {
                					DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                					Date date = new Date();
                					
                					int count = mDbAdapter.fetchAllRows().getCount();
                					mDbAdapter.createRow(count + 1, 
                							point.getLat(), 
                							point.getLon(), 
                							textTitle.getText().toString(), 
                							dateFormat.format(date));
                					
                					dlg.dismiss();
                					((MainActivity)mMapController.getContext()).addPointsOnLayer();
                				}
                			});
                			Button dlgBtnCancel = (Button) dlg.findViewById(R.id.btn_point_cancel);                			
                			dlgBtnCancel.setOnClickListener(new OnClickListener() {
                				@Override
                				public void onClick(View v) {
                					dlg.dismiss();
                				}
                			});                 
                			dlg.show();
                        }
                    });
                    
                    AlertDialog alert = builder.create();
                    alert.show();
        	    }
        	});*/
        	
            break;
		}
	}
}
