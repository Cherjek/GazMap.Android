package ru.zch.gasstation.domain;

import java.util.Iterator;
import java.util.List;

import ru.zch.gasstation.services.ServicePoints;

public enum PointFields {
	Name(0, "Название") {
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getName(), point.getName(), sender);
		}

		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			point.setName(change.getValueNew());
		}
	},
	Address(1, "Адрес") {
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getAddress(), point.getAddress(), sender);
		}
		
		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			point.setAddress(change.getValueNew());
		}
	},
	Latitude(2, "Широта") {
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getLat(), point.getLat(), sender);
		}
		
		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			Float value = Float.parseFloat(change.getValueNew());
			point.setLat(value);
		}
	},
	Longitude(3, "Долгота"){
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getLon(), point.getLon(), sender);
		}

		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			Float value = Float.parseFloat(change.getValueNew());
			point.setLon(value);
		}
	},
	PricePropan(4, "Цена пропан") {
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getPriceOfType(1), point.getPriceOfType(1), sender);
		}
		
		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			Float value = Float.parseFloat(change.getValueNew());
			
			for(Price price : point.getPrices()){
				if(price.getId().getTid() == 1){
					price.setPrice(value);
					break;
				}
			}
		}
	},	
	PriceMetan(5, "Цена метан") {
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getPriceOfType(2), point.getPriceOfType(2), sender);
		}
		
		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			Float value = Float.parseFloat(change.getValueNew());
			
			for(Price price : point.getPrices()){
				if(price.getId().getTid() == 2){
					price.setPrice(value);
					break;
				}
			}
		}

	},
	Worktime(6, "Режим работы") {
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getWorktime(), point.getWorktime(), sender);
		}
		
		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			point.setWorktime(change.getValueNew());	
		}
	},
	Type(7, "Тип топлива") {
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getType(), point.getType(), sender);
		}
		
		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			int pointType = Integer.parseInt(change.getValueNew());
			List<Type> types = service.getTypes();
			
			//add/remove
			for(Type type : types){
				int id = type.getId(); 
				if((id & pointType) == 0){
					remove(point, id);
				}else{
					//add new price if not exists
					if(isExist(point, id) == false){
						PointTypesId pointTypesId = new PointTypesId(point.getId(), id);
						point.getPrices().add(new Price(pointTypesId));
					}
				}
			}
		}
		
		private boolean isExist(Point point, int id){
			boolean result = false;
			
			for(Price price : point.getPrices()){
				if(price.getId().getTid() == id){
					result = true;
					break;
				}
			}
			
			return result;
		}
		
		private void remove(Point point, int id){
			Iterator<Price> iterator = point.getPrices().iterator();
			while(iterator.hasNext() == true){
				Price price = iterator.next();
				if(price.getId().getTid() == id){
					iterator.remove();
					break;
				}
			}
		}
	},
	State(8, "Статус") {
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getState(), point.getState(), sender);
		}
		
		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			Integer state = Integer.parseInt(change.getValueNew());
			point.setState(state);
		}
	},
	Cards(9, "Прием банковских карт") {
		@Override
		public Change make(Point original, Point point, Device sender) {
			return make(original, original.getIsCardAccepted(), point.getIsCardAccepted(), sender);
		}
		
		@Override
		public void apply(Point point, Change change, ServicePoints service) {
			Byte isCardAccepted = Byte.parseByte(change.getValueNew());
			point.setIsCardAccepted(isCardAccepted);
		}
	};
	
	public final int id;
	public final String name;
	
	PointFields(int id, String name){
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Creates a new change for the field. 
	 * @param original point
	 * @param point which have to be compared to the original one
	 * @return null if there is no change detected
	 */
	public abstract Change make(Point original, Point point, Device sender);
	
	/**
	 * Applies the Change to the Point
	 * @param point
	 * @param change
	 */
	public abstract void apply(Point point, Change change, ServicePoints service);
	
	protected Change make(Point original, Object valueOriginal, Object valueNew, Device sender){
		//Object to string original
		String originalString = null;
		if(valueOriginal != null){
			originalString = valueOriginal.toString();
		}

		//Object Ыto string new
		String newString = null;
		if(valueNew != null){
			newString = valueNew.toString();
		}
		
		return make(original, originalString, newString, sender);
	}
		
	/**
	 * Build a change base on string representations
	 * @param valueOriginal
	 * @param valueNew
	 * @param sender
	 * @return null if there is no change
	 */
	protected Change make(Point original, String valueOriginal, String valueNew, Device sender){
		Change change = null;
		if(	(valueOriginal != null && valueOriginal.equals(valueNew) == false) ||
			 (valueOriginal == null && valueNew != null)){
			
			change = new Change(original, sender, id, valueNew);
		}
		return change;
	}
}
