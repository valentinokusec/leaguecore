package com.leaguelove.services;


import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.Search;
import com.cloudant.client.api.model.FindByIndexOptions;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.cloudant.client.api.model.SearchResult;
import com.leaguelove.domain.LeadBoardModel;
import com.leaguelove.repository.CrudRepository;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Region;



@Service
public class LeadBoardServiceImpl implements LeadBoardService {
	
    private static final String API_KEY="172d9054-b070-449d-bb68-cbbe94f29e7c";
	  
	CloudantClient client;
	Database db;
	CouchDbInstance dbInstance ;
	 CrudRepository repo;
    public LeadBoardServiceImpl()
    {
    	try {
			HttpClient authenticatedHttpClient = new StdHttpClient.Builder()
			        .url("https://tino.cloudant.com")
			        .username("tino")
			        .password("ireliaftw1")
			        .build();

		 dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
		 CouchDbConnector dbc = new StdCouchDbConnector("league_of_legends_votes", dbInstance);
		 repo = new CrudRepository(dbc);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	RiotAPI.setRegion(Region.EUW);
        RiotAPI.setAPIKey(API_KEY);
        
        client= ClientBuilder.account("tino")
                .username("tino")
                .password("ireliaftw1")
                .build();
        db = client.database("league_of_legends_votes", false);
	}
	
	@Override
	public JSONArray getVotes(int pagination) {
		// TODO Auto-generated method stub
		
		
	
		
		List<LeadBoardModel> docs=repo.getAll();

		docs.sort(new LeadBoardModel());
		
		JSONArray votes_array= new JSONArray();
		int i=0;
		for (Iterator iterator = docs.iterator(); iterator.hasNext();) {
			i++;
			JSONObject leadboard=new JSONObject();
			LeadBoardModel leadBoardModel = (LeadBoardModel) iterator.next();	
			leadboard.put("votes", leadBoardModel.getVotes());
			leadboard.put("place", i);
			leadboard.put("name", leadBoardModel.get_id());
			leadboard.put("profile_icon", leadBoardModel.getProfile_icon());
			votes_array.put(leadboard);
			System.out.println(leadBoardModel.getVotes());
		}
		
//		System.out.println("Revision of the document is: " + doc.getVotes());
//		List<LeadBoardModel> votes;
//		
//		db.createIndex("votes", null,
//                null, new IndexField[]{ new IndexField("_id",SortOrder.asc),
//                       new IndexField("votes",SortOrder.asc)});
//		                                     
//		votes= db.findByIndex("{selector: { type: votes},sort:[_id,votes]}",
//				 LeadBoardModel.class);
		
		 
		return votes_array;
	}

	@Override
	public JSONArray getVotesForSummoner(String name) {
		// TODO Auto-generated method stub
		LeadBoardModel doc = new LeadBoardModel();
		doc=repo.get(name);
		JSONArray votes=new JSONArray();
		votes.put(doc);
	
		return votes;
	}

}

