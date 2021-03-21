package com.gasstation.mapgs;

import java.util.List;

import ru.yandex.yandexmapkit.MapController;
import ru.yandex.yandexmapkit.map.GeoCode;
import ru.yandex.yandexmapkit.map.GeoCodeListener;
import ru.yandex.yandexmapkit.overlay.balloon.BalloonItem;
import ru.yandex.yandexmapkit.overlay.drag.DragAndDropItem;
import ru.yandex.yandexmapkit.overlay.drag.DragAndDropOverlay;
import ru.yandex.yandexmapkit.utils.ScreenPoint;

public class DragAndDropOverlayEx extends DragAndDropOverlay implements GeoCodeListener {

	public String GeoAddress = null;
	
	public DragAndDropOverlayEx(MapController arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
	public DragAndDropItem getDragAndDropItem() {
		DragAndDropItem itemDrag = null;
		List<Object> items = this.getOverlayItems();
		for(Object item : items) {
    		if (item instanceof DragAndDropItem) {
    			itemDrag = (DragAndDropItem)item;
    			break;
    		}
    	}
		return itemDrag;
	}
	
	public void updateGeoCode() {
		DragAndDropItem itemDrag = getDragAndDropItem();
		if (itemDrag != null) {
			getMapController().setPositionAnimationTo(itemDrag.getGeoPoint());
			getMapController().getDownloader().getGeoCode(this, itemDrag.getGeoPoint());//getMapController().getGeoPoint(new ScreenPoint(x, y)));		
		}
	}

	@Override
	public boolean onUp(float x, float y) {
		DragAndDropItem itemDrag = getDragAndDropItem();
		if (itemDrag != null) {
			getMapController().getDownloader().getGeoCode(this, itemDrag.getGeoPoint());//getMapController().getGeoPoint(new ScreenPoint(x, y)));		
		}
		return super.onUp(x, y);
	}
	
	@Override
    public boolean onFinishGeoCode(final GeoCode geoCode) {
        if (geoCode != null){
        	
            getMapController().getMapView().post(new Runnable() {
                @Override
                public void run() {                    
                    // Set the additional balloon offset
                    //balloonDrar.setOffsetX(offsetX);
                    // Add the balloon model to the object
                    DragAndDropItem itemDrag = getDragAndDropItem();
            		if (itemDrag != null) {
            			BalloonItem balloonDrag = itemDrag.getBalloonItem();
            			balloonDrag.setText(geoCode.getDisplayName());
            			GeoAddress = geoCode.getDisplayName();
            		}
                }
            });
        }
        return true;
    }
}
