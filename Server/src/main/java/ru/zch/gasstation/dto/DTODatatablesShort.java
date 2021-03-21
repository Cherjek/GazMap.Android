package ru.zch.gasstation.dto;

import java.util.Date;

import ru.zch.gasstation.domain.Point;
import ru.zch.gasstation.services.ServicePoints;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DTODatatablesShort extends DTOPointBase{

	private Date _modified = null;
	private Integer _pendingChanges = 0;
			
	private static boolean getBoolean(Byte value){
		if(value == null || value == 0){
			return false;
		}else{
			return true;
		}
	}
	
	public DTODatatablesShort(){}
	
	public static DTODatatablesShort make(Point point){
		DTODatatablesShort dto = new DTODatatablesShort();
		
		dto._id = point.getId();
		
		dto._isDeleted = getBoolean(point.getIsDeleted());
		
		dto._isAccepted = getBoolean(point.getIsAccepted());
		
		dto._state = point.getState();

		dto._prices = point.getPrices();
		
		dto._type = getType(point);
		
		dto._name = point.getName();
		
		dto._raiting = point.getRating();	
		dto._pendingChanges = point.getChanges().size();
		dto._modified = point.getModified();
		
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
	public Boolean getHasChanges(){
//		if(_pendingChanges != null && _pendingChanges > 0){
//			return true;
//		}else{
//			return false;
//		}
		return null;
	}
	
	public void setHasChanges(boolean value){}
	
	@JsonProperty("9")
	public String isAccepted() {
		if(_isAccepted == true)
			return "Да";
		return "Нет";
	}
	public void setAccepted(String isAccepted) {
		if(isAccepted != null & isAccepted.equalsIgnoreCase("Да") == true){
			_isAccepted = true;
		}else{
			_isAccepted = false;
		}
	}
	
	@JsonProperty("2")
	public String getState(){
		switch(_state){
			case 1:
				return "Работает";
			case 2:
				return "Не работает";
		}
	
		return null;

	}
	public void setState(String state){
		switch(state){
			case "Работает":
				_state = 1;
				break;
			case "Не работает":
				_state = 2;
				break;
		}
	}
	
	@JsonProperty("3")
	public String getType(){
		switch(_type){
			case 1:
				return "Пропан";
			case 2:
				return "Метан";
			case 3:
				return "Пропан + Метан";
			case 4:
				return "Сервис";
		}
		
		return null;
	}
	
	public void setType(String type){
		switch(type){
			case "Пропан":
				_type = 1;
				break;
			case "Метан":
				_type = 2;
				break;
			case "Пропан + Метан":
				_type = 3;
				break;
			case "Сервис":
				_type = 4;
				break;
		}
	}

	
	@JsonProperty("4")
	public String getName(){
		return _name;
	}
	public void setName(String name){
		_name = name;
	}
	
	@JsonProperty("1")
	public Integer getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}

//	@JsonProperty("6")
//	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd.MM.yyyy HH:mm", timezone="GMT+4" )
//	public Date getCreated() {
//		return _created;
//	}
//
//	public void setCreated(Date created) {
//		_created = created;
//	}
	
	@JsonProperty("7")
	public Float getRating() {
		return _raiting;
	}
	public void setRating(Float value) {
		_raiting = value;
	}
	
	@JsonProperty("5")
	public Float getPricePropan(){
		return getPrice(1);
	}
	
	public void setPricePropan(Float price){
		setPrice(1, price);
	}
	
	@JsonProperty("6")
	public Float getPriceMetan(){
		return getPrice(2);
	}
	
	public void setPriceMetan(Float price){
		setPrice(2, price);
	}
	
	@JsonProperty("10")
	public String isDeleted() {
		if(_isDeleted == true){
			return "Нет";
		}else{
			return "Да";	
		}
	}
	
	public void setDeleted(String isDeleted) {
		if(isDeleted != null & isDeleted.equalsIgnoreCase("Нет") == true){
			_isDeleted = true;
		}else{
			_isDeleted = false;
		}
	}

	@JsonProperty("8")
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd.MM.yyyy HH:mm", timezone="GMT+4" )
	public Date getModified() {
		return _modified;
	}
	
	public void setModified(String isDeleted) {}
	
	@JsonProperty("changes")
	public Integer getChanges() {
		return _pendingChanges;
	}
	
	public void setChanges() {}
}
