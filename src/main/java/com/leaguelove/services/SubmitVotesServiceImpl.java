package com.leaguelove.services;


import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.IndexField;
import com.cloudant.client.api.model.IndexField.SortOrder;
import com.leaguelove.domain.VoteModel;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.summoner.Summoner;



@Service
public class SubmitVotesServiceImpl implements SubmitVotesService {
	
    private static final String API_KEY="172d9054-b070-449d-bb68-cbbe94f29e7c";
	  
	CloudantClient client;
	Database db;
//	CouchDbInstance dbInstance ;
//	 CrudRepository repo;
    public SubmitVotesServiceImpl()
    {
//    	try {
//			HttpClient authenticatedHttpClient = new StdHttpClient.Builder()
//			        .url("https://tino.cloudant.com")
//			        .username("tino")
//			        .password("ireliaftw1")
//			        .build();
//
//		 dbInstance = new StdCouchDbInstance(authenticatedHttpClient);
//		 CouchDbConnector dbc = new StdCouchDbConnector("league_of_legends_users", dbInstance);
//		 repo = new CrudRepository(dbc);
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    	
    	RiotAPI.setRegion(Region.EUW);
        RiotAPI.setAPIKey(API_KEY);
        
        client= ClientBuilder.account("tino")
                .username("tino")
                .password("ireliaftw1")
                .build();
        db = client.database("league_of_legends", false);
	}
@Override
public JSONArray getVotes(String name) {
	// TODO Auto-generated method stub
		Summoner summoner = RiotAPI.getSummonerByName(name);
  	
	  	List<MatchReference> listmatch=summoner.getMatchList();
	   

    	
    	   													
									  		
    	List<VoteModel> movies_list = null;

    	Long matchid=listmatch.get(0).getID();
		//long matchid=listmatch.get(i).getID();
    	JSONArray last_match=new JSONArray();
    	String name_uppercase= name.substring(0,1).toUpperCase()+name.substring(1);
    	try {
    		movies_list = db.findByIndex("{selector: { match_id: "+matchid+",to:"+name_uppercase+"}}",
    				VoteModel.class);
    		if (movies_list.size()==0) {
    			last_match.put("nodata");
			}
    		else
    		 {
    		last_match=new JSONArray(movies_list.toString());}
		} catch (Exception e) {
			
		}
		
	
	    
	 
		return last_match;

}
@Override
public JSONArray submitVotes(String[] data) {
	// TODO Auto-generated method stub
	
	for (int i = 0; i < data.length; i++) {
		String string = data[i];
		VoteModel lm=db.find(VoteModel.class, string);
		
		
		lm.setConfirmed(true);
		
		db.update(lm);
		
	}
	return new JSONArray().put("ok");
}
	

	

}

