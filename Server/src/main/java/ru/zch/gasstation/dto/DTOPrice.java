package ru.zch.gasstation.dto;

import ru.zch.gasstation.domain.Price;

public class DTOPrice {
	protected Float _price = 0f;
	protected Integer _type = 0;
	protected Long _date = null;
	public DTOPrice(){}
	
	public DTOPrice(Price price){
		_price = price.getPrice();
		_type = price.getId().getTid();
		
		if(price.getModified() != null){
			_date = price.getModified().getTime();
		}
	}

	public Float getPrice() {
		return _price;
	}

	public void setPrice(Float price) {
		_price = price;
	}

	public Integer getType() {
		return _type;
	}

	public void setType(Integer type) {
		_type = type;
	}

	public Long getDate() {
		return _date;
	}

	public void setDate(Long date) {
		_date = date;
	}
}
