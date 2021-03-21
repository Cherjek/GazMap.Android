package ru.zch.gasstation.dto;

import ru.zch.gasstation.domain.Device;
import ru.zch.gasstation.domain.Point;
import ru.zch.gasstation.domain.Vote;
import ru.zch.gasstation.log.Log;

public class DTOVoteResult extends DTOVote{
	protected Float _rating = .0f;
	protected Integer _votes = 0;
	
	public DTOVoteResult(Vote vote){
		//point related
		Point point = vote.getPoint();
		if(point != null){
			_pointId = point.getId();
			_rating = point.getRating();
			_votes = point.getVoteCount();
		}else{
			Log.w("Cannot create DTO Vote Result for vote without Point!");
		}
		
		//device related
		Device device = vote.getDevice();
		if(device != null){
			_deviceName = device.getName();
		}else{
			Log.w("Cannot create DTO Vote Result for vote without Device!");
		}
		
		_vote = vote.getVote();
	}

	public Float getRating() {
		if(_votes > 0 && _rating < 1){
			return 1f;
		}
		
		return _rating;
	}

	public void setRating(Float rating) {
		_rating = rating;
	}

	public Integer getVotes() {
		return _votes;
	}

	public void setVotes(Integer votes) {
		_votes = votes;
	}
}
