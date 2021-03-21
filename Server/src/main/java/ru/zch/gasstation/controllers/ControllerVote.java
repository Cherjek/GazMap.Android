package ru.zch.gasstation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.zch.gasstation.domain.Vote;
import ru.zch.gasstation.dto.DTOVote;
import ru.zch.gasstation.dto.DTOVoteResult;
import ru.zch.gasstation.services.ServiceVotes;

@Controller
@RequestMapping("/voting")
public class ControllerVote {
	@Autowired
	private ServiceVotes _service;

	/**
	 * Retrieves Points modified or added after supplied time stamp
	 * @param stamp if not specified all points will be retrieved
	 */
	@RequestMapping(value="/get", method = RequestMethod.GET)
	@ResponseBody
	public DTOVoteResult getPoints(@RequestParam(value = "name") String name, @RequestParam(value = "id") int pointId){
		Vote vote = _service.getVote(pointId, name);
		
		DTOVoteResult result = null;
		if(vote != null){
			result = new DTOVoteResult(vote);
		}
		
		return result;
	}
	
	/**
	 * Retrieves Points modified or added after supplied time stamp
	 * @param stamp if not specified all points will be retrieved
	 */
	@RequestMapping(value="/vote", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public DTOVoteResult getVote(@RequestBody DTOVote vote){
		Vote resultVote = _service.setVote(vote.getPointId(), vote.getDeviceName(), vote.getVote());
		
		DTOVoteResult result = null;
		if(resultVote != null){
			result = new DTOVoteResult(resultVote);
		}
		
		return result;
	}
}
