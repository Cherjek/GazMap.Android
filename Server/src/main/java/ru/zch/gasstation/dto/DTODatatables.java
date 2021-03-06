package ru.zch.gasstation.dto;

import java.util.Date;

import ru.zch.gasstation.domain.Point;
import ru.zch.gasstation.services.ServicePoints;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DTODatatables extends DTOPointBase{

	private Date _created = null;
	private Integer _pendingChanges = 0;
			
	private static boolean getBoolean(Byte value){
		if(value == null || value == 0){
			return false;
		}else{
			return true;
		}
	}
	
	public DTODatatables(){}
	
	public static DTODatatables make(Point point){
		DTODatatables dto = new DTODatatables();
		
		dto._id = point.getId();
		
		dto._latitude = point.getLat();
		dto._longitude = point.getLon();
		
		dto._isDeleted = getBoolean(point.getIsDeleted());
		
		dto._isCardAccepted = getBoolean(point.getIsCardAccepted());
		dto._isAccepted = getBoolean(point.getIsAccepted());
		
		dto._address = point.getAddress();
		dto._state = point.getState();

		dto._prices = point.getPrices();
		
		dto._type = getType(point);
		
		dto._name = point.getName();
		
		dto._worktime = point.getWorktime();
		dto._created = point.getCreated();

		dto._raiting = point.getRating();	
		dto._pendingChanges = point.getChanges().size();
		
		return dto;
	}
	
	public Point toPoint(ServicePoints service){
		Point point = null;
		if(_id == null || _id < 0){
			point = new Point();
		}else{
			point = service.getPoint(_id);
		}
		
		point.setLat(_latitude);
		point.setLon(_longitude);
		point.setName(_name);
		point.setAddress(_address);
		point.setState(_state);
		
		setPrices(point, service);
		
		point.setIsCardAccepted((byte) (_isCardAccepted == true ? 1 : 0));
		point.setIsDeleted((byte) (_isDeleted == true ? 1 : 0));
		point.setIsAccepted((byte) (_isAccepted == true ? 1 : 0));
		point.setWorktime(_worktime);
		
		return point;
	}
	
	@JsonProperty("DT_RowId")
	public Integer getIdRow() {
		return _id;
	}
	public void setIdRow(int id) {
		_id = id;
	}
	

	@JsonProperty("0")
	public String getIsAccepted() {
		if(_isAccepted == true)
			return "????";
		return "??????";
	}
	public void setIsAccepted(String isAccepted) {
		if(isAccepted != null & isAccepted.equalsIgnoreCase("????") == true){
			_isAccepted = true;
		}else{
			_isAccepted = false;
		}
	}
	
	@JsonProperty("1")
	public String getState(){
		switch(_state){
			case 1:
				return "????????????????";
			case 2:
				return "???? ????????????????";
		}
	
		return null;

	}
	public void setState(String state){
		switch(state){
			case "????????????????":
				_state = 1;
				break;
			case "???? ????????????????":
				_state = 2;
				break;
		}
	}
	
	@JsonProperty("2")
	public String getType(){
		switch(_type){
			case 1:
				return "????????????";
			case 2:
				return "??????????";
			case 3:
				return "???????????? + ??????????";
			case 4:
				return "????????????";
		}
		
		return null;
	}
	
	public void setType(String type){
		switch(type){
			case "????????????":
				_type = 1;
				break;
			case "??????????":
				_type = 2;
				break;
			case "???????????? + ??????????":
				_type = 3;
				break;
			case "????????????":
				_type = 4;
				break;
		}
	}

	
	@JsonProperty("3")
	public String getName(){
		return _name;
	}
	public void setName(String name){
		_name = name;
	}
	
	@JsonProperty("4")
	public String getAddress(){
		return _address;
	}
	public void setAddress(String address){
		_address = address;
	}	
	
	@JsonProperty("5")
	public String getWorktime() {
		return _worktime;
	}

	public void setWorktime(String worktime) {
		_worktime = worktime;
	}
	
	@JsonProperty("6")
	public String getIsCardAccepted() {
		if(_isCardAccepted == true)
			return "????";
		return "??????";
	}
	public void setIsCardAccepted(String isCardAccepted) {
		if(isCardAccepted != null && isCardAccepted.equalsIgnoreCase("????") == true){
			_isCardAccepted = true;
		}else{
			_isCardAccepted = false;
		}
	}

	@JsonProperty("7")
	public Float getLatitude() {
		return _latitude;
	}
	public void setLatitude(Float latitude) {
		_latitude = latitude;
	}
	@JsonProperty("8")
	public Float getLongitude() {
		return _longitude;
	}
	public void setLongitude(Float longitude) {
		_longitude = longitude;
	}

	@JsonProperty("9")
	public Integer getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}

	@JsonProperty("10")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd.MM.yyyy HH:mm", timezone="GMT+4" )
	public Date getCreated() {
		return _created;
	}

	public void setCreated(Date created) {
		_created = created;
	}
	
	@JsonProperty("11")
	public Float getRating() {
		return _raiting;
	}
	public void setRating(Float value) {
		_raiting = value;
	}
	
	@JsonProperty("12")
	public Float getPricePropan(){
		return getPrice(1);
	}
	
	public void setPricePropan(Float price){
		setPrice(1, price);
	}
	
	@JsonProperty("13")
	public Float getPriceMetan(){
		return getPrice(2);
	}
	
	public void setPriceMetan(Float price){
		setPrice(2, price);
	}
	
	@JsonProperty("14")
	public String getIsDeleted() {
		if(_isDeleted == true){
			return "??????";
		}else{
			return "????";	
		}
	}
	
	public void setIsDeleted(String isDeleted) {
		if(isDeleted != null & isDeleted.equalsIgnoreCase("??????") == true){
			_isDeleted = true;
		}else{
			_isDeleted = false;
		}
	}
	
	@JsonProperty("changes")
	public Integer getChanges() {
		return _pendingChanges;
	}
	
	public void setChanges() {}
}
