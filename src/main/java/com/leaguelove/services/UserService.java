package com.leaguelove.services;

import java.util.List;

import org.json.JSONArray;


public interface UserService {
	
	public JSONArray signUp(String name,String password,String summonername);
	
	public JSONArray signIn(String name,String password);

}
