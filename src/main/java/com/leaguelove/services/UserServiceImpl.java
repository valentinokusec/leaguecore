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
import com.leaguelove.domain.UserModel;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Region;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.summoner.Summoner;



@Service
public class UserServiceImpl implements UserService {
	
    private static final String API_KEY="172d9054-b070-449d-bb68-cbbe94f29e7c";
	  
	CloudantClient client;
	Database db;
//	CouchDbInstance dbInstance ;
//	 CrudRepository repo;
    public UserServiceImpl()
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
        db = client.database("league_of_legends_users", false);
	}
	

	@Override
	public JSONArray signUp(String name, String password, String summonername) {
		// TODO Auto-generated method stub
		JSONArray data=new JSONArray();
	
			
			List<UserModel> movies_list = null;
			movies_list = db.findByIndex("{selector: { _id:"+name+"}}",
    				UserModel.class);
    		
    		
			if (movies_list.size()!=0) {
				data.put("user_exist");
			}
			else {
				
				movies_list = db.findByIndex("{selector: { summoner:"+summonername+"}}",
	    				UserModel.class);
				if (movies_list.size()!=0) {
					data.put("summoner_exist");
				}
				else {
					UserModel user=new UserModel();
					user.set_id(name);
					user.setPassword(password);
					user.setSummoner(summonername);
					user.setAuthorazied(false);
					db.save(user);
				}
			}
    		
    		//long matchid=listmatch.get(i).getID();
    		
    	
		
			
		return data;
	}

	@Override
	public JSONArray signIn(String name, String password) {
		
		JSONArray data=new JSONArray();
		List<UserModel> movies_list = null;
		movies_list = db.findByIndex("{selector: { _id:"+name+",password:"+password+"}}",
				UserModel.class);
		
		
		if (movies_list.size()==0) {
			data.put("wrong_data");
		}
		else {
			data = new JSONArray(movies_list.toString());
		}
	
		return data;
	}

}

