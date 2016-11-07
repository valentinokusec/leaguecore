package com.leaguelove.domain;

import java.util.Comparator;

import org.ektorp.support.CouchDbDocument;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LeadBoardModel extends CouchDbDocument implements Comparator<LeadBoardModel>{

	private String _id;
	
	private Integer votes;
	
	private int profile_icon;
	
	private String type;
	
	private String _rev;
	
	@JsonProperty("profile_icon")
	public int getProfile_icon() {
		return profile_icon;
	}
	
	@JsonProperty("_id")
	public String get_id() {
		return _id;
	}

	@JsonProperty("_id")
	public void set_id(String _id) {
		this._id = _id;
	}

	

	@JsonProperty("profile_icon")
	public void setProfile_icon(int profile_icon) {
		this.profile_icon = profile_icon;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("votes")
	 public Integer getVotes() {
		return votes;
	}

	@JsonProperty("votes")
	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	 
	 public int compare(LeadBoardModel o1, LeadBoardModel o2)
	    {
	       return o2.getVotes().compareTo(o1.getVotes());
	   }
	 public String toString() {
		 return "{ \"_id\": \"" + _id + "\",\"_rev\":\"" +_rev+ "\",\"votes\":\"" + votes + "\",\"type\":\"" + type+ "\",\"profile_icon\":\"" + profile_icon +"\"}";
		  }

	
}
