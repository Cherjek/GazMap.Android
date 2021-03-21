package com.gasstation.mapgs;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gasstation.MainActivity;
import com.gasstation.R;
import com.gasstation.common.AsyncTaskEx;
import com.gasstation.common.HttpRequest;
import com.gasstation.common.Settings;
import com.gasstation.common.Utils;
import com.gasstation.db.GSDbAdapter;
import com.gasstation.db.GSDbModelUpdater;
import com.gasstation.model.GPoint;
import com.gasstation.model.MainTab;
import com.gasstation.model.OilTypes;
import com.gasstation.model.PriceTab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.balloon.OnBalloonListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class BaseBalloonItem extends BalloonItem implements OnBalloonListener{
	
	protected TextView textView;
	protected TextView textStatusView;
	protected TextView textTypeView;
	protected TextView textScheduleView;
	protected TextView balloonAddressView;
	protected TextView chckIsBankView;
	protected TextView textViewNavigate;
	protected TextView textViewInfo;
	
	protected TextView textRatingView;
	protected TextView textRatingTextView;
	protected ImageView btnMinView;
	protected ImageView btnPlusView;
	protected LinearLayout llStarratingImageView;
	
	protected LinearLayout llPrices;
	//protected ListView lstPrices1;
	//protected ListView lstPrices2;
	
	private Context mContext;
	private GPoint mPoint;
	
	private GSDbAdapter mDbAdapter;
	
	public BaseBalloonItem(Context context, GeoPoint geoPoint, GPoint point) {			
        super(context, geoPoint);
        this.mContext = context;
        this.mPoint = point;
        this.mDbAdapter = ((MainActivity)context).getDbAdapter();
    }
	
	@Override
    public void inflateView(Context context){

        LayoutInflater inflater = LayoutInflater.from( context );
        model = (ViewGroup)inflater.inflate(R.layout.balloon_image_layout, null);
        textView = (TextView)model.findViewById( R.id.balloon_text_view ); 
        textStatusView = (TextView)model.findViewById( R.id.balloon_status_view ); 
        textTypeView = (TextView)model.findViewById( R.id.balloon_type_view ); 
        textScheduleView = (TextView)model.findViewById( R.id.balloon_schedule_view ); 
        balloonAddressView = (TextView)model.findViewById( R.id.balloon_address_view );
        chckIsBankView = (TextView)model.findViewById( R.id.balloon_isbank_view ); 
        textViewNavigate = (TextView)model.findViewById( R.id.balloon_text_navigate );
        textViewInfo = (TextView)model.findViewById( R.id.balloon_text_info );
        
        textRatingView = (TextView)model.findViewById( R.id.balloon_rating_view );
        textRatingTextView = (TextView)model.findViewById( R.id.balloon_ratingtext_view );
        btnMinView = (ImageView)model.findViewById( R.id.balloon_btnmin_view );
        btnPlusView = (ImageView)model.findViewById( R.id.balloon_btnplus_view );
        llStarratingImageView = (LinearLayout)model.findViewById( R.id.ll_starrating_image_view );
        
        llPrices = (LinearLayout)model.findViewById( R.id.ll_price_point );  
        //lstPrices1 = (ListView)model.findViewById( R.id.lst_price_point1 );  
        //lstPrices2 = (ListView)model.findViewById( R.id.lst_price_point2 );
    }
	
	public void setBalloonContent() {
		if (mPoint != null) {			
			List<MainTab> items = new ArrayList<MainTab>();
	    	items.add(new MainTab((long) 1, "Работает"));
	    	items.add(new MainTab((long) 2, "Не работает"));	    	
	    	MainTab item = items.get(mPoint.statusId - 1);
	    	if (item != null) {
	    		textStatusView.setText(item.Name);
	    	}
			textStatusView.setVisibility(mPoint.statusId == 2 ? View.VISIBLE : View.GONE);
			
				LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(
						mPoint.statusId == 1 ? 350 : 175, 
						LayoutParams.WRAP_CONTENT);
				textView.setLayoutParams(vp);
				textView.setText(mPoint.title);
	    	
	    	items = OilTypes.GetOilTypes(false);
	    	item = null;
	    	for(MainTab mt : items) {
	    		if (mt.Id == mPoint.typeId) {
	    			item = mt;//items.get(mPoint.typeId - 1);
	    			break;
	    		}
	    	}
	    	if (item != null) {
	    		textTypeView.setText(item.Name);
	    	}
	    	textTypeView.setVisibility(mPoint.typeId == 4 ? View.VISIBLE : View.GONE);
	    	
	    	textViewInfo.setVisibility(mPoint.typeId < 4 ? View.VISIBLE : View.GONE);
	    	
	    	textScheduleView.setText("Режим работы: " + (mPoint.schedule == null || (mPoint.schedule != null && mPoint.schedule.equals("null")) ? "" : mPoint.schedule));
	    	textScheduleView.setVisibility(mPoint.statusId == 1 ? View.VISIBLE : View.GONE);
	    	
	    	if (mPoint.address == null || (mPoint.address != null && mPoint.address.isEmpty()) || (mPoint.address != null && mPoint.address.equals("null"))) {
	    		balloonAddressView.setVisibility(View.GONE);
	    	}
	    	else {
	    		balloonAddressView.setText("Адрес: " + mPoint.address);
	    	}	    	
	    	
	    	chckIsBankView.setText(mPoint.isBankCard == 1 ? "Банковские карты принимаются" : "Банковские карты не принимаются");
	    	chckIsBankView.setVisibility(mPoint.statusId == 1 ? View.VISIBLE : View.GONE);
	    	setRating();
		}
	}
	
	public void setOnViewClickListener(){        
        //setOnBalloonViewClickListener(R.id.balloon_text_view, this);
        setOnBalloonViewClickListener(R.id.balloon_text_navigate, this);
        setOnBalloonViewClickListener(R.id.balloon_text_info, this);
        
        setOnBalloonViewClickListener(R.id.balloon_btnmin_view, this);
        setOnBalloonViewClickListener(R.id.balloon_btnplus_view, this);
        
        setOnBalloonListener(this);
    }
    
    public void onBalloonViewClick(BalloonItem item, View view) {
        // TODO Auto-generated method stub
        
        switch (view.getId()) {
        
        case R.id.balloon_text_view:
        	break;
        case R.id.balloon_text_navigate:
        	AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	    	dialog.setMessage(R.string.dlg_balloon_qnavigate);
	    	dialog.setNegativeButton(android.R.string.no, null);
	    	dialog.setPositiveButton(android.R.string.yes, new OnClickListener() {
	          @Override
	          public void onClick(DialogInterface dialog, int which) {
	        	// Создаем интент для построения маршрута
	  	        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
	  	        intent.setPackage("ru.yandex.yandexnavi");

	  	        PackageManager pm = mContext.getPackageManager();
	  	        List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);

	  	        // Проверяем, установлен ли Яндекс.Навигатор
	  	        if (infos == null || infos.size() == 0) {
	  	            // Если нет - будем открывать страничку Навигатора в Google Play
	  	            intent = new Intent(Intent.ACTION_VIEW);
	  	            intent.setData(Uri.parse("market://details?id=ru.yandex.yandexnavi"));
	  	        } else {
	  	        	GeoPoint point = null;
	  	        	if (mContext instanceof MainActivity) {
	  	        		point = ((MainActivity)mContext).getMyLocation();
	  	        	}
	  	        	if (point != null) {
	  		            intent.putExtra("lat_from", point.getLat());
	  		            intent.putExtra("lon_from", point.getLon());
	  		            intent.putExtra("lat_to", mPoint.lat);
	  		            intent.putExtra("lon_to", mPoint.lng);
	  	        	}
	  	        }

	  	        // Запускаем нужную Activity
	  	        mContext.startActivity(intent);
	          }
	        });
	    	dialog.show();
	    	
	        break;  
	        
	    case R.id.balloon_text_info:
	    	if (mContext instanceof MainActivity) {
	    		((MainActivity)mContext).showSelectPointInfo(mPoint);
	    	}
	    	
	    	break;
	    case R.id.balloon_btnmin_view:
	    	
	    	setCurrentRating(-1);
        	break;
	    case R.id.balloon_btnplus_view:
	    	
	    	setCurrentRating(1);
        	break;
	    }
        

    }
    
    private void setRating(Integer rating) {    	
    	Resources res = mContext.getResources();
    	if (rating == null) {
    		textRatingTextView.setText(R.string.rating_text_null);
    		btnMinView.setImageDrawable(res.getDrawable(R.drawable.ratdismin));
    		btnPlusView.setImageDrawable(res.getDrawable(R.drawable.ratdisplus));
    	}
    	else if (rating == 1) {
    		textRatingTextView.setText(R.string.rating_text_good);
    		btnMinView.setImageDrawable(res.getDrawable(R.drawable.ratdismin));
    		btnPlusView.setImageDrawable(res.getDrawable(R.drawable.ratenbplus));
    	}
    	else if (rating == -1) {
    		textRatingTextView.setText(R.string.rating_text_bad);
    		btnMinView.setImageDrawable(res.getDrawable(R.drawable.ratenbmin));
    		btnPlusView.setImageDrawable(res.getDrawable(R.drawable.ratdisplus));
    	}     	
    	setChange(true);    	
    }
    
    private void setPrices() {    	
    	
    	int visibility = mPoint.typeId >= 4 ? View.GONE : View.VISIBLE;    	
    	llPrices.setVisibility(visibility);
    	if (visibility == View.GONE) {
    		return;
    	}    
    	
		Cursor cPrices = mDbAdapter.fetchPrices(mPoint.id);
		cPrices.moveToFirst();
		while(!cPrices.isAfterLast()) {				
			Long typeId = cPrices.getLong(cPrices.getColumnIndex(GSDbAdapter.KEY_TYPE));
			Double price = cPrices.getDouble(cPrices.getColumnIndex(GSDbAdapter.KEY_PRICE));
			String date = cPrices.getString(cPrices.getColumnIndex(GSDbAdapter.KEY_DATE));
			
			TextView tvType = null;
			LinearLayout llPrice = null;
			TextView tvDate = null;
			if (cPrices.getPosition() == 0) {
				tvType = (TextView)model.findViewById(R.id.balloon_lstprice_type_view1);
				llPrice = (LinearLayout)model.findViewById(R.id.balloon_lstprice_price_view1);
				tvDate = (TextView)model.findViewById(R.id.balloon_lstprice_date_view1);
			}
			else if (cPrices.getPosition() == 1) {
				tvType = (TextView)model.findViewById(R.id.balloon_lstprice_type_view2);
				llPrice = (LinearLayout)model.findViewById(R.id.balloon_lstprice_price_view2);
				tvDate = (TextView)model.findViewById(R.id.balloon_lstprice_date_view2);
			}
			
			List<MainTab> items = OilTypes.GetOilTypes(false);			
	    	for(MainTab mt : items) {
	    		if (mt.Id == typeId) {
	    			tvType.setText(mt.Name);
	    			break;
	    		}
	    	}
			
			setPrice(Utils.getDecimalValueFormat("#####00.00", price), llPrice, typeId);
			
			try {
				SimpleDateFormat dtf = new SimpleDateFormat("yyyy.MM.dd");
				Date _date = dtf.parse(date);
				dtf = new SimpleDateFormat("dd.MM.yy");
				tvDate.setText("Цена от " + dtf.format(_date));
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			cPrices.moveToNext();
		}
    }
    
    private void setRating() {
    	Resources res = mContext.getResources();
    	if (mPoint.rating != null) {
    		for(int i = 0; i < 5; i++) {
    			ImageView image = (ImageView)llStarratingImageView.getChildAt(i);
    			if (i < Math.round(mPoint.rating))
    				image.setImageDrawable(res.getDrawable(R.drawable.rating_star_full));
    			else
    				image.setImageDrawable(res.getDrawable(R.drawable.rating_star_empty));
    		}
    	}
    	textRatingView.setText("(" + (mPoint.voteCount == null ? 0 : mPoint.voteCount) + ")");
    	setRating(mPoint.vote);
    }
    
    private void setCurrentRating(Integer vote) {
    	TelephonyManager telephonyManager;                                            
        telephonyManager  = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        
    	UpdateCurrentRating asnkUpdate = new UpdateCurrentRating(mContext, false);
    	asnkUpdate.execute(vote, telephonyManager.getDeviceId(), mPoint.id);
    }
    
    @Override
    public void onBalloonShow(BalloonItem balloonItem) {
    	
    	setPrices();
    	
    	TelephonyManager telephonyManager;                                            
        telephonyManager  = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
        
    	UpdateTotalRating asncTotlal = new UpdateTotalRating(mContext, true);
    	asncTotlal.execute(mPoint.id, telephonyManager.getDeviceId());
    }

    
    public void onBalloonHide(BalloonItem balloonItem) {
        // TODO Auto-generated method stub

    }

   
    public void onBalloonAnimationStart(BalloonItem balloonItem) {
        // TODO Auto-generated method stub

    }

    
    public void onBalloonAnimationEnd(BalloonItem balloonItem) {
        // TODO Auto-generated method stub

    }

    class UpdateCurrentRating extends AsyncTaskEx {
    	public UpdateCurrentRating(Context context, boolean isHidden) {
			super(context, isHidden);
			// TODO Auto-generated constructor stub
		}
    	
    	@Override
		protected Object doInBackground(Object... params) {
    		String result = null;
    		try {
				Settings settings = new Settings(context);            	
            	String ip = settings.getString(Settings.IP_ADDRESS, "213.141.148.66");
            	
				HttpRequest request = new HttpRequest("http://" + ip + "/gazstation/voting/vote");
				result = request.requestPost(
						"{ 'vote' : " + (Integer)params[0] +
						", 'deviceName' : '" + (String)params[1] + "'" +
						", 'pointId' : " + (Long)params[2] +
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
					point.id = jsonObj.getLong("pointId");
					point.vote = jsonObj.getInt("vote");
					point.rating = jsonObj.getDouble("rating");
					point.voteCount = jsonObj.getInt("votes");
										
					GSDbModelUpdater db = new GSDbModelUpdater(context);
					db.Update(point, GSDbModelUpdater.TypeUpdate.Update);
					
					mPoint.vote = point.vote;
					mPoint.rating = point.rating;
					mPoint.voteCount = point.voteCount;
					setRating();
					((MainActivity)mContext).getMapController().showBalloon(BaseBalloonItem.this);
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exception = e;
				}
			}
			super.onPostExecute(result);
    	}
    }
    
    class UpdateTotalRating extends AsyncTaskEx {
    	public UpdateTotalRating(Context context, boolean isHidden) {
			super(context, isHidden);
			// TODO Auto-generated constructor stub
		}
    	
    	@Override
		protected Object doInBackground(Object... params) {
    		String result = null;
    		try {
    			Settings settings = new Settings(context);
    			String ip = settings.getString(Settings.IP_ADDRESS, "213.141.148.66");
    			HttpRequest request = new HttpRequest("http://" + ip + "/gazstation/voting/get?id=" + (Long)params[0] + "&name=" + (String)params[1]);
    			result = request.requestGet();
    		}
    		catch(Exception e) {
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
					point.id = jsonObj.getLong("pointId");
					point.vote = jsonObj.getInt("vote");
					point.rating = jsonObj.getDouble("rating");
					point.voteCount = jsonObj.getInt("votes");
										
					GSDbModelUpdater db = new GSDbModelUpdater(context);
					db.Update(point, GSDbModelUpdater.TypeUpdate.Update);
					
					mPoint.vote = point.vote;
					mPoint.rating = point.rating;
					mPoint.voteCount = point.voteCount;
					setRating();
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					exception = e;
				}
			}
			super.onPostExecute(result);
    	}
    }

    public void setPrice(String values, LinearLayout ll, Long typeId) {
		float density = mContext.getApplicationContext().getResources().getDisplayMetrics().density;
		int hDp = 28;
		int wDp = 15;
		int newHeight = (int) (density * hDp);
		int newWidth = (int) (density * wDp);
		
		Paint paint = new Paint();
		Rect bounds = new Rect();

		paint.setTypeface(Typeface.DEFAULT);// your preference here
		paint.setTextSize(40);// have this the same as your text size

		String text = "A";

		paint.getTextBounds(text, 0, text.length(), bounds);

		newWidth =  bounds.width();
		newHeight =  (int) (newWidth / 0.72);//		
		int newWidthtDot =  bounds.width() / 2;
		
		ll.removeViews(0, ll.getChildCount());
		
		/*ImageView imgType = new ImageView(getContext());
		LinearLayout.LayoutParams vpType = new LinearLayout.LayoutParams(newWidth, newHeight);
		vpType.rightMargin = 5;
		imgType.setLayoutParams(vpType);
		Bitmap bmType = getBitmapFromRes(typeId == 1 ? R.drawable.sred_point : R.drawable.sblue_point);
		imgType.setImageBitmap(bmType);
		ll.addView(imgType);*/
		
		for(int i = 0; i < values.length(); i++) {
			Bitmap bm = getBitmap(values.charAt(i));
			
			ImageView img = new ImageView(mContext);
			LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(
					values.charAt(i) == '.' || values.charAt(i) == ',' ? newWidthtDot : newWidth, 
					newHeight);
			img.setLayoutParams(vp);
			img.setImageBitmap(bm);
			ll.addView(img);
		}
		Bitmap bm = getBitmap('c');
		ImageView img = new ImageView(mContext);
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(
				/*LayoutParams.WRAP_CONTENT*/newWidth, 
				/*LayoutParams.WRAP_CONTENT*/newHeight);
		vp.leftMargin = 2;
		img.setLayoutParams(vp);
		img.setImageBitmap(bm);
		ll.addView(img);
	}
	
	public Bitmap getBitmap(char c) {
		switch(c) {
			case 'c': 
				return getBitmapFromRes(R.drawable.currency);
			case '0': 
				return getBitmapFromRes(R.drawable.n0);
			case '1': 
				return getBitmapFromRes(R.drawable.n1);
			case '2': 
				return getBitmapFromRes(R.drawable.n2);
			case '3': 
				return getBitmapFromRes(R.drawable.n3);
			case '4': 
				return getBitmapFromRes(R.drawable.n4);
			case '5': 
				return getBitmapFromRes(R.drawable.n5);
			case '6': 
				return getBitmapFromRes(R.drawable.n6);
			case '7': 
				return getBitmapFromRes(R.drawable.n7);
			case '8': 
				return getBitmapFromRes(R.drawable.n8);
			case '9': 
				return getBitmapFromRes(R.drawable.n9);
			case ',':
			case '.': 
				return getBitmapFromRes(R.drawable.dot);
		}
		return null;
	}
	
	public Bitmap getBitmapFromRes(int res) {
		InputStream is = mContext.getResources().openRawResource(res);
        return BitmapFactory.decodeStream(is);
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
}
