package com.leaguelove.services;

import java.util.List;

import org.json.JSONArray;

import com.leaguelove.domain.LeadBoardModel;

public interface LeadBoardService {
	
	public JSONArray getVotes(int pagination);
	
	public JSONArray getVotesForSummoner(String name);

}
