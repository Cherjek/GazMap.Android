package ru.zch.gasstation.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.zch.gasstation.dao.DAODevice;
import ru.zch.gasstation.dao.DAOPoint;
import ru.zch.gasstation.dao.DAOVote;
import ru.zch.gasstation.domain.Device;
import ru.zch.gasstation.domain.Point;
import ru.zch.gasstation.domain.Vote;
import ru.zch.gasstation.log.Log;

@Service
@Transactional
public class ServiceVotes {
	@Autowired
	DAODevice daoDevice;
	
	@Autowired
	DAOVote daoVote;
	
	@Autowired
	DAOPoint daoPoint;
	
	public Vote setVote(int pointId, String deviceName, int value){
		Vote vote = null;
		
		//normalize vote
		Device device = daoDevice.get(deviceName);
		Point point = daoPoint.get(pointId);
		
		if(point != null){
			//update vote if already exists
			vote = daoVote.getVote(device, point);
			if(vote == null){
				vote = new Vote(point, device, value);
			}
			
			//set new Vote value
			vote.setVote(value);
			
			//save to DB
			daoVote.update(vote);
		}else{
			Log.wF("Cannot vote for point which does not exists. Point id is %d", pointId);
		}
		
		return vote;
	}
	
	/**
	 * @return null if vote does not exists
	 */
	public Vote getVote(int pointId, String deviceName){
		Vote vote = null;
		
		//normalize vote
		Device device = daoDevice.get(deviceName);
		Point point = daoPoint.get(pointId);
		
		if(point != null){
			//update vote if already exists
			vote = daoVote.getVote(device, point);
		}else{
			Log.wF("Cannot find vote for not exist point. Point id is %d", pointId);
		}
		
		return vote;
	}
}
