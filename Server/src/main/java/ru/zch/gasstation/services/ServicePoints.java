package ru.zch.gasstation.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.zch.gasstation.dao.DAOChanges;
import ru.zch.gasstation.dao.DAODevice;
import ru.zch.gasstation.dao.DAOPoint;
import ru.zch.gasstation.dao.DAOPrice;
import ru.zch.gasstation.dao.DAOType;
import ru.zch.gasstation.domain.Change;
import ru.zch.gasstation.domain.Device;
import ru.zch.gasstation.domain.Point;
import ru.zch.gasstation.domain.PointFields;
import ru.zch.gasstation.domain.Price;
import ru.zch.gasstation.domain.Type;
import ru.zch.gasstation.log.Log;

@Service
@Transactional
public class ServicePoints {
	@Autowired
	DAOPoint _point;

	@Autowired
	DAOPrice _price;
	
	@Autowired
	DAOType _type;
	
	@Autowired
	DAODevice _device;
	
	@Autowired
	DAOChanges _changes;
	
	/**
	 * Retrieves all points
	 */
	@Transactional
	public List<Point> getPointsAll(){
		List<Point> points = _point.getListAll();
		
		//build array of points
		if(points == null){
			points = new ArrayList<Point>();
		}
				
		return points;
	}

	/**
	 * Retrieves all points which is not deleted
	 */
	@Transactional
	public List<Point> getPoints(){
		List<Point> points = _point.getList();
		
		//build array of points
		if(points == null){
			points = new ArrayList<Point>();
		}
				
		return points;
	}
	
	/**
	 * Retrieves points modified after specified date
	 */
	@Transactional
	public List<Point> getPoints(long stamp){
		List<Point> points = _point.getList(stamp);
		
		//build array of points
		if(points == null){
			points = new ArrayList<Point>();
		}
		
		return points;
	}
	
	/**
	 * Load point by id
	 */
	@Transactional
	public Point getPoint(int id){
		Point point = _point.get(id);
		return point;
	}

	/**
	 * Really saves point with whole data
	 */
	@Transactional
	public Point save(Point point){
		Set<Price> prices = point.getPrices();

		//update or add new point
		Point saved = _point.update(point);
		
		//update id
		for(Price price : prices){
			price.getId().setPid(saved.getId());
		}
		
		//save data
		_price.save(saved.getId(), prices);
		
		return saved;
	}

	/**
	 * If point already exists this method builds list of changes for point and saves them to DB
	 * @param point which should be compared to the original one
	 * @return original point with new changes in its changes-list.
	 */
	@Transactional
	public Point saveChanges(Point point){
		//its a new point we've to save it as is
		if(point.getId() == null){
			return save(point);
		}
		
		//load original point 
		Point original = _point.get(point.getId());
		Device sender = _device.get("LEGACY_SENDER");///uses while mobile device does not send anything
		
		//collect changes
		for(PointFields field : PointFields.values()){
			Change change = field.make(original, point, sender);
			
			//add new change to collection
			if(change != null){
				_changes.save(change);
				original.getChanges().add(change);
			}
		}
		
		return original;
	}

	/**
	 * Applies the change to the Point
	 * @param id of the change
	 * @return
	 */
	@Transactional
	public void changeCancel(int id){
		_changes.remove(id);
	}
	
	/**
	 * Applies the change to the Point
	 * @param id of the change
	 * @return
	 */
	@Transactional
	public Point changeApply(int id){
		Point point = null;
		Change change = _changes.get(id);
		
		if(change != null){
			point = change.apply(this);
			point = save(point);
			_changes.remove(id);
		}else{
			Log.wF("Could not find change with id %d", id);
		}
		
		return point;
	}
	
	/**
	 * Retrieves all changes to the Point
	 * @param id of the point
	 * @return
	 */
	@Transactional
	public Set<Change> getChanges(int id){
		Set<Change> changes = null;
		Point point = getPoint(id);
		
		if(point != null){
			changes = point.getChanges();
		}else{
			Log.wF("Point does not exists %d", id);
		}
		
		return changes;
	}
	
	@Transactional
	public List<Type> getTypes(){
		return _type.getAll();
	}
	
	@Transactional
	public List<Price> getPrices(int pointId){
		return _price.get(pointId);
	}
}
