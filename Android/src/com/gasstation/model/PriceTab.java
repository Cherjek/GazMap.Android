package com.gasstation.model;

public class PriceTab extends MainTab {

	public Long TypeId;
	public Object Value;
	public String Date;
	//id - заправка
	//name - имя типа заправки
	//typeId - id типа заправка
	public PriceTab(Long id, String name, Long typeId, Object value) {
		super(id, name);
		TypeId = typeId;
		Value = value;
	}
	
	public PriceTab(Long id, String name, Long typeId, Object value, String date) {
		super(id, name);
		TypeId = typeId;
		Value = value;
		Date = date;
	}

	public void setValue(Object value) {
		Value = value;
	}
}
