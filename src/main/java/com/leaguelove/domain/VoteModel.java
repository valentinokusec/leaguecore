package com.leaguelove.domain;


public class VoteModel {
	
	private String _id;

	private String to;
	
	private String _rev;
	
	private String data;
	
	private Integer profile_icon;
	
	private Long match_id;
	
	private boolean confirmed;
	
	public VoteModel(String to, String from, String data, Long match_id,int i,boolean confirmed)
	{
		this.from=from;
		
		this.confirmed=confirmed;
		
		this.to=to;
		
		this.data=data;
		
		this.match_id=match_id;
		
		this.profile_icon=i;
	}
public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
private String from;
	
	public Long getMatch_id() {
	return match_id;
}
public void setMatch_id(Long match_id) {
	this.match_id = match_id;
}
	public String getData() {
	return data;
}
public void setData(String data) {
	this.data = data;
}
public Integer getProfile_icon() {
	return profile_icon;
}
public void setProfile_icon(Integer profile_icon) {
	this.profile_icon = profile_icon;
}


	 public boolean isConfirmed() {
		return confirmed;
	}
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	public String toString() {
		 return "{\"_id\": \"" + _id +"\",\"_rev\":\"" +_rev+"\" ,\"to\": \"" + to + "\",\"from\":\"" + from + "\",\"data\":\"" + data + "\",\"profile_icon\":"+profile_icon+",\"match_id\":"+match_id+",\"confirmed\":"+confirmed+"}";
		  }
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
}
