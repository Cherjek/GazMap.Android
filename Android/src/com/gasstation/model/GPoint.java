package com.gasstation.model;

import java.util.Date;

public class GPoint {

	public Long id;
	public Double lat;
	public Double lng;
	public String title;	
	public Date date;
	
	public String address;
	public Integer statusId;
	public Long typeId;
	public String schedule;
	public Integer isBankCard;
	
	public PriceTab[] prices;
	public Integer vote;
	public Double rating;
	public Integer voteCount;
}
