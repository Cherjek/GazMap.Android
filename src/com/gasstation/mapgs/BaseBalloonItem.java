package com.gasstation.mapgs;

import com.gasstation.R;
import com.gasstation.model.GPoint;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.balloon.OnBalloonListener;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class BaseBalloonItem extends BalloonItem implements OnBalloonListener{
	
	protected TextView textView;
	
	Context mContext;
	GPoint mPoint;
	
	public BaseBalloonItem(Context context, GeoPoint geoPoint, GPoint point) {			
        super(context, geoPoint);
        this.mContext = context;
        this.mPoint = point;
    }
	
	@Override
    public void inflateView(Context context){

        LayoutInflater inflater = LayoutInflater.from( context );
        model = (ViewGroup)inflater.inflate(R.layout.balloon_image_layout, null);        
        textView = (TextView)model.findViewById( R.id.balloon_text_view );        
        setText(textView.getText());
    }
	
	public void setOnViewClickListener(){        
        setOnBalloonViewClickListener(R.id.balloon_text_view, this);
    }
    
    public void onBalloonViewClick(BalloonItem item, View view) {
        // TODO Auto-generated method stub
        /*AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        switch (view.getId()) {
        
        case R.id.balloon_text_view:
            dialog.setTitle("Click text");
            break;    
        }
        dialog.show();*/

    }
    
    @Override
    public void onBalloonShow(BalloonItem balloonItem) {
        // TODO Auto-generated method stub
    	textView.setText(mPoint.title);
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
}
