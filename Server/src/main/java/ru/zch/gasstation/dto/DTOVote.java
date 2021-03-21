package ru.zch.gasstation.dto;

public class DTOVote {
	protected Integer _pointId = 0;
	protected String _deviceName = null;
	protected Integer _vote = 0;
	
	public Integer getPointId() {
		return _pointId;
	}

	public void setPointId(Integer pointId) {
		_pointId = pointId;
	}

	public String getDeviceName() {
		return _deviceName;
	}

	public void setDeviceName(String deviceName) {
		_deviceName = deviceName;
	}

	public Integer getVote() {
		return _vote;
	}

	public void setVote(Integer vote) {
		_vote = vote;
	}
}
