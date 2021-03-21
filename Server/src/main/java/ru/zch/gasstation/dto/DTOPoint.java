package ru.zch.gasstation.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import ru.zch.gasstation.domain.Point;
import ru.zch.gasstation.domain.PointTypesId;
import ru.zch.gasstation.domain.Price;
import ru.zch.gasstation.services.ServicePoints;

public class DTOPoint extends DTOPointBase {
	
	public static DTOPoint make(Point point){
		DTOPoint dto = new DTOPoint();
		
		dto._id = point.getId();
		
		dto._latitude = point.getLat();
		dto._longitude = point.getLon();
		
		dto._isDeleted = point.getIsDeleted() == 1 ? true : false;
		dto._isCardAccepted = point.getIsCardAccepted() == 1 ? true : false;
		dto._isAccepted = point.getIsAccepted() == 1 ? true : false;
		
		dto._address = point.getAddress();
		dto._state = point.getState();
		
		dto._prices = point.getPrices();
		dto._type = getType(point);
		
		dto._name = point.getName();
		
		dto._worktime = point.getWorktime();
		dto._raiting = point.getRating();
		dto._votes = point.getVoteCount();
		
		return dto;
	}
	
	public  Point toPoint(ServicePoints service){
		Point point = null;
		if(_id != null && _id > 0){
			point = service.getPoint(_id);
		}
		
		if(point == null){
			point = new Point();
		}
		
		point.setLat(_latitude);
		point.setLon(_longitude);
		point.setName(_name);
		point.setAddress(_address);
		point.setState(_state);
		
//		setPrices(point, service);
		point.setPrices(_prices);
		
		point.setIsCardAccepted((byte) (_isCardAccepted == true ? 1 : 0));
		point.setIsDeleted((byte) (_isDeleted == true ? 1 : 0));
		point.setWorktime(_worktime);
		return point;
	}
	
	public Integer getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}
	
	public Float getLatitude() {
		return _latitude;
	}
	public void setLatitude(Float latitude) {
		_latitude = latitude;
	}
	
	public Float getLongitude() {
		return _longitude;
	}
	public void setLongitude(Float longitude) {
		_longitude = longitude;
	}
	public boolean isDeleted() {
		return _isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		_isDeleted = isDeleted;
	}
	
	public boolean isCardAccepted() {
		return _isCardAccepted;
	}
	public void setIsCardAccepted(boolean isCardAccepted) {
		_isCardAccepted = isCardAccepted;
	}

	public boolean isAccepted() {
		return _isAccepted;
	}
	public void setIsAccepted(boolean isAccepted) {
		_isAccepted = isAccepted;
	}

	
	public Integer getState(){
		return _state;
	}
	public void setState(Integer state){
		_state = state;
	}
	
	public Integer getType(){
		return _type;
	}
	public void setType(Integer type){
		_type = type;
	}
	
	public String getName(){
		return _name;
	}
	
	public void setName(String name){
		_name = name;
	}
	
	public String getAddress(){
		return _address;
	}
	
	public void setAddress(String address){
		_address = address;
	}

	public String getWorktime() {
		return _worktime;
	}

	public void setWorktime(String worktime) {
		_worktime = worktime;
	}
	
	public Float getRating(){
		if(_votes > 0 && _raiting < 1){
			return 1f;
		}
		
		return _raiting;
	}
	
	public Integer getVoteCount(){
		return _votes;
	}
	
	public List<DTOPrice> getPrices(){
		List<DTOPrice> result = null;
		
		if(_prices != null){
			result = new ArrayList<>();
			
			Iterator<Price> price = _prices.iterator();

			while(price.hasNext() == true){
				result.add(new DTOPrice(price.next()));
			}
		}
		
		return result;
	}
	
	public void setPrices(List<DTOPrice> prices){
		_prices = new HashSet<>();
		if(prices != null){
			for(DTOPrice dto : prices){
				PointTypesId id = new PointTypesId(_id, dto.getType());
				Price price = new Price(id, dto.getPrice());
				
				_prices.add(price);
			}
		}
	}
}
