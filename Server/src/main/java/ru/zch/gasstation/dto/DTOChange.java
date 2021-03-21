package ru.zch.gasstation.dto;

import java.util.Date;

import ru.zch.gasstation.domain.Change;

public class DTOChange {
	protected int _id = 0;
	protected int _fieldId = 0;
	protected String _text = null;
	protected Date _created = null;
	
	public static DTOChange make(Change change){
		DTOChange dto = new DTOChange();
		dto._id = change.getId();
		dto._fieldId = change.getField();
		dto._text = change.getValueNew();
		dto._created = change.getCreated();
		
		return dto;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

	public int getFieldId() {
		return _fieldId;
	}

	public void setFieldId(int fieldId) {
		_fieldId = fieldId;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		_text = text;
	}

	public Date getCreated() {
		return _created;
	}

	public void setCreated(Date created) {
		_created = created;
	}
}
