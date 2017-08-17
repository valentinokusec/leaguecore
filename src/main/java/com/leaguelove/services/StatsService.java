package com.leaguelove.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;



@Service
public interface StatsService {
	public JSONObject getStats(String name) throws MalformedURLException, IOException;
} 
