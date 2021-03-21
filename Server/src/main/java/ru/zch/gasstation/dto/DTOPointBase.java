package ru.zch.gasstation.dto;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ru.zch.gasstation.domain.Point;
import ru.zch.gasstation.domain.PointTypesId;
import ru.zch.gasstation.domain.Price;
import ru.zch.gasstation.domain.Type;
import ru.zch.gasstation.log.Log;
import ru.zch.gasstation.services.ServicePoints;


public class DTOPointBase {
	protected Integer _id = 0;
	
	protected Float _latitude = 0f;
	protected Float _longitude = 0f;
	
	protected boolean _isDeleted = false;

	protected String _name = null;
	protected String _address = null;

	//1 - work; 2 - out of service;
	protected Integer _state = 0;
	
	//1 - propan; 2 - metand; 3 - propern + metan
	protected Integer _type = 1;
	protected boolean _isCardAccepted = true; 
	protected boolean _isAccepted = true; 
	
	protected String _worktime = null;
	protected Float _raiting = .0f;
	protected int _votes = 0;
	
	protected Set<Price> _prices = null;
	
	/**
	 * Type of point as mask of all types
	 */
	protected static int getType(Point point){
		int mask = 0;
		
		//get type
		Set<Price> prices = point.getPrices();
		if(prices != null){
			for(Price price : prices){
				mask += price.getId().getTid();
			}
		}else{
			Log.w("No prices found");
		}
		
		return mask;
	}
	
	/**
	 * Setup prices from DTO to point
	 */
	protected void setPrices(Point point, ServicePoints service){
		//remove old prices
		Set<Price> prices = point.getPrices();
		prices.clear();

		//add prices for each type in DTO 
		if(_type != null){
			List<Type> types = service.getTypes();
			for(Type type : types){
				if((type.getId() & _type) != 0){
					PointTypesId id = new PointTypesId(_id, type.getId());
					
					float price = getPrice(type.getId());
					prices.add(new Price(id, price));					
				}
			}
		}
	}
	
	protected Float getPrice(int typeId){
		Iterator<Price> iterator = _prices.iterator();

		while(iterator.hasNext() == true){
			Price price = iterator.next();
			if(price.getId().getTid() == typeId){
				return price.getPrice();
			}
		}
		
		return 0f;
	}
	
	protected void setPrice(int typeId, Float value){
		if(_prices != null){
			Iterator<Price> iterator = _prices.iterator();
	
			while(iterator.hasNext() == true){
				Price price = iterator.next();
				if(price.getId().getTid() == typeId){
					price.setPrice(value);
					break;
				}
			}
		}else{
			_prices = new HashSet<>();
		}
		
		//price didn't found
		PointTypesId id = new PointTypesId(_id, typeId);
		_prices.add(new Price(id, value));
	}
	
	protected static boolean getBoolean(Integer value){
		if(value == null || value == 0){
			return false;
		}else{
			return true;
		}	
	}
	
	protected static Integer getInteger(Boolean value){
		if(value == null){
			return null;
		}
		
		if(value == true){
			return 1;
		}else{
			return 0;
		}
	}
}
