package ru.zch.gasstation.dto;

import java.util.ArrayList;
import java.util.List;

import ru.zch.gasstation.domain.PointFields;

public class DTOPointField {
	protected Integer _id = 0;
	protected String _name = null;
	
	/**
	 * Make whole list of fields
	 */
	public static final List<DTOPointField> make(){
		List<DTOPointField> items = new ArrayList<>();
		
		for(PointFields field : PointFields.values() ){
			DTOPointField dto = new DTOPointField(field.id, field.name);
			items.add(dto);
		}
		
		return items;
	}
	
	DTOPointField(int id, String name){
		_id = id;
		_name = name;
	}
	
	public Integer getId() {
		return _id;
	}
	public void setId(Integer id) {
		_id = id;
	}
	public String getName() {
		return _name;
	}
	public void setName(String name) {
		_name = name;
	}
}