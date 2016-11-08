package com.leaguelove.services;

import org.json.JSONArray;

public interface MainService {
	
	public JSONArray getGeneralData(String name);
	
	public JSONArray getRecentHistory(String name);
	
	public JSONArray getMatch(Long matchid);
	
	public JSONArray getGeneralHistory(String name,int a, String champion);
	
	public JSONArray getChampionHistory(String name,String championname);

	public JSONArray getRandomMessage();

	public String CheckSummonerName(String name);

}
