package com.leaguelove.services;

import java.util.List;

import org.json.JSONArray;



public interface VotesService {
	public JSONArray getVotes(String name,int pagination);

	public JSONArray getLastVotes(String string, Long string2);

	public JSONArray getVotesById(String string, Long string2);
} 
