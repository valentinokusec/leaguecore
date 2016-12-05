package com.leaguelove.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;



@Service
public interface LiveStatsService {
	public JSONArray getLiveStats(String name);
	public JSONArray getHistory(String name);
	public JSONArray getHistoryRoles(JSONObject data_get);
	public JSONArray getSort(JSONArray jsonArray);
} 
