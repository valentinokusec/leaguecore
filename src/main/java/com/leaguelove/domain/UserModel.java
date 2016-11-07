package com.leaguelove.domain;

import java.util.Comparator;


import com.fasterxml.jackson.annotation.JsonProperty;

public class UserModel {

	private String _id;
	
	private String password;
	
	private String summoner;
	
	private boolean authorazied;
	
	
	@JsonProperty("_id")
	public String get_id() {
		return _id;
	}

	@JsonProperty("_id")
	public void set_id(String _id) {
		this._id = _id;
	}

	

	@JsonProperty("summoner")
	public String getSummoner() {
		return summoner;
	}
	@JsonProperty("summoner")
	public void setSummoner(String summoner) {
		this.summoner = summoner;
	}
	@JsonProperty("password")
	public String getPassword() {
		return password;
	}
	@JsonProperty("password")
	public void setPassword(String password) {
		this.password = password;
	}
	@JsonProperty("authorazied")
	public boolean isAuthorazied() {
		return authorazied;
	}
	@JsonProperty("authorazied")
	public void setAuthorazied(boolean authorazied) {
		this.authorazied = authorazied;
	}


	public String toString() {
		 return "{\"_id\": \"" + _id +"\",\"authorazied\":\"" +authorazied+"\" ,\"password\": \"" + password + "\",\"summoner\":" + summoner + "}";
		  }


	
}
