package ru.zch.gasstation.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.zch.gasstation.domain.Change;
import ru.zch.gasstation.domain.Point;
import ru.zch.gasstation.domain.PointTypesId;
import ru.zch.gasstation.domain.Price;
import ru.zch.gasstation.dto.DTOChange;
import ru.zch.gasstation.dto.DTODatatables;
import ru.zch.gasstation.dto.DTODatatablesShort;
import ru.zch.gasstation.dto.DTOId;
import ru.zch.gasstation.dto.DTOPoint;
import ru.zch.gasstation.dto.DTOPointField;
import ru.zch.gasstation.services.ServicePoints;

@Controller
@RequestMapping("/points")
public class ControllerService {
	private static final int DAY = 24 * 60 * 60;
	
	@Autowired
	private ServicePoints _service;
	
	/**
	 * Retrieves Points modified or added after supplied time stamp
	 * @param stamp if not specified all points will be retrieved
	 */
	@RequestMapping(value="/get", method = RequestMethod.GET)
	@ResponseBody
	public List<DTOPoint> getPoints(@RequestParam(value = "stamp", required = false) Long stamp){
		//to make sure TZ will not play any role we'll load changes for last Stamp - Day
		List<Point> points = stamp == null ? _service.getPoints() : _service.getPoints(stamp - DAY);
		
		List<DTOPoint> dtos = new ArrayList<>();
		for(Point point : points){
			dtos.add(DTOPoint.make(point));
		}
		
		return dtos;
	}
	
	@RequestMapping(value="/add", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public DTOPoint getPoints(@RequestBody DTOPoint dto){
		Point point = dto.toPoint(_service);
		_service.saveChanges(point);
		
		dto = DTOPoint.make(point);
		
		return dto;
	}
	
	/**
	 * Retrieves all point and shows them in table with ability to edit them
	 */
	@RequestMapping(value="/editor/get/list", method = RequestMethod.GET)
	@ResponseBody
	public Object getPoints(){
		class ToSend{
			public List<DTODatatablesShort> aaData = new ArrayList<>();
		}

		ToSend data = new ToSend();

		//get list of dtos
		List<Point> points = _service.getPointsAll();
		
		for(Point point : points){
			data.aaData.add(DTODatatablesShort.make(point));
		}

		return data;
	}
	
	/**
	 * Retrieves all point and shows them in table with ability to edit them
	 */
	@RequestMapping(value="/editor/get/point", method = RequestMethod.GET)
	@ResponseBody
	public DTODatatables getPoint(@RequestParam(value = "id", required = true) Integer id){
		//get list of dtos
		Point point = _service.getPoint(id);
		
		return DTODatatables.make(point);
	}

	/**
	 * Retrieves all changes for the point
	 */
	@RequestMapping(value="/editor/get/changes", method = RequestMethod.GET)
	@ResponseBody
	public List<DTOChange> getChanges(@RequestParam(value = "id", required = true) Integer id){
		List<DTOChange> changes = new ArrayList<>();
		
		//get list of all changes
		Set<Change> changesList = _service.getChanges(id);
		
		//build list of changes DTOs
		for(Change change : changesList){
			changes.add(DTOChange.make(change));
		}
		
		return changes;
	}
	
	/**
	 * Retrieves all point's fields with names and ids
	 */
	@RequestMapping(value="/editor/get/fields", method = RequestMethod.GET)
	@ResponseBody
	public List<DTOPointField> getFields(){
		return DTOPointField.make();
	}
	
	/**
	 * Force saves changes for the Point
	 * @param dto
	 * @return
	 */
	@RequestMapping(value="/editor/save", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public DTODatatables getPoints(@RequestBody DTODatatables dto){
		Point point = dto.toPoint(_service);
		_service.save(point);
		
		dto = DTODatatables.make(point);
		
		return dto;
	}
	
	/**
	 * Apply the change to the Point
	 * @param id of the change
	 * @return point with the change
	 */
	@RequestMapping(value="/editor/apply", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public DTODatatables changeApply(@RequestBody DTOId id){		
		Point point = _service.changeApply(id.getId());
		
		DTODatatables dto = DTODatatables.make(point);
		
		return dto;
	}

	/**
	 * Cancels the change 
	 * @param id of the change
	 * @return point with the change
	 */
	@RequestMapping(value="/editor/cancel", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public void changeCancel(@RequestBody DTOId id){		
		_service.changeCancel(id.getId());
	}
	
//	/**
//	 * Retrieves all point and shows them in table with ability to edit them
//	 */
//	@RequestMapping(value="/editor/save", produces = "text/html; charset=UTF-8")
//	@ResponseBody
//	public String getPointss(@RequestParam("value") String value, @RequestParam("row_id") Integer id, @RequestParam("column") Integer column){
//		Point point = _service.getPoint(id);
//		
//		//only dto knows how to parse that shit
//		DTODatatables dto = DTODatatables.make(point);
//		
//		if(point != null){
//			switch (column) {
//				case 0:
//					dto.setAccepted(value);
//					break;
//				case 1:
//					dto.setState(value);
//					break;
//				case 2:
//					dto.setType(value);
//					break;
//				case 3:
//					dto.setName(value);
//					break;
//				case 4:
//					dto.setAddress(value);
//					break;
//				case 5:
//					dto.setWorktime(value);
//					break;
//				case 6:
//					dto.setIsCardAccepted(value);
//					break;
//				case 7:
//					dto.setLatitude(Float.valueOf(value));
//					break;
//				case 8:
//					dto.setLongitude(Float.valueOf(value));
//					break;
//				case 9:
//					dto.setId(Integer.valueOf(value));
//					break;
//
//				case 10:
//					//we could not modify date of creation
//					break;
//
//				case 12:
//					dto.setPricePropan(Float.valueOf(value));
//					break;
//				case 13:
//					dto.setPriceMetan(Float.valueOf(value));
//				case 14:
//					dto.setDeleted(value);
//					break;
//			}
//		}
//		//apply changes to point
//		point = dto.toPoint(_service);
//		
//		_service.save(point);
//		return value;
//	}
	
	/**
	 * Adds new Point into DB and return its ID
	 */
	@RequestMapping(value="/editor/add")
	@ResponseBody
	public DTODatatablesShort getNewPointId(){
		Point point = new Point();
		
		point.setIsAccepted((byte) 0);
		point.setIsDeleted((byte) 0);
		point.setState(1);
		
		_service.save(point);
		
		//add default Type
		PointTypesId priceId = new PointTypesId(point.getId(), 1);
		point.getPrices().add(new Price(priceId, 0f));
		_service.save(point);
		
		DTODatatablesShort dto = DTODatatablesShort.make(point);
		return dto;
	}
}
