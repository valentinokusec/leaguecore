package com.leaguelove.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.springframework.stereotype.Service;

import com.leaguelove.domain.SummonerProfile;

@Service
public interface LiveStatsService {
	public JSONArray getLiveStats(String name);
	public JSONArray getHistory(String name);
} 
