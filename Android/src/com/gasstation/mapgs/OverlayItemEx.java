package com.gasstation.mapgs;

import com.gasstation.model.GPoint;

import android.graphics.drawable.Drawable;
import ru.yandex.yandexmapkit.overlay.OverlayItem;
import ru.yandex.yandexmapkit.utils.GeoPoint;

public class OverlayItemEx extends OverlayItem {

	private GPoint gPoint;
	public OverlayItemEx(GeoPoint arg0, Drawable arg1, GPoint gPoint) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
		this.gPoint = gPoint;
	}
	
	public GPoint getGPoint() {
		return gPoint;
	}

}
