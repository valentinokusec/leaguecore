
package com.leaguelove.services;

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
public class VotesServiceImpl implements VotesService{

	
	private static final String API_KEY="172d9054-b070-449d-bb68-cbbe94f29e7c";
	 
	CloudantClient client;
	Database db;
    public VotesServiceImpl()
    {
    	RiotAPI.setRegion(Region.EUW);
        RiotAPI.setAPIKey(API_KEY);
        
        client= ClientBuilder.account("tino")
                .username("tino")
                .password("ireliaftw1")
                .build();

		//Note: for Cloudant Local or Apache CouchDB use:
		//ClientBuilder.url(new URL("yourCloudantLocalAddress.example"))
		//.username("exampleUser")
		//.password("examplePassword")
		//.build();
		
		//Show the server version
		System.out.println("Server Version: " + client.serverVersion());
		db = client.database("league_of_legends", false);
		//Get a List of all the databases this Cloudant account
		
	}
    
	@Override
	public JSONArray getVotes(String name, int pagination) {
		// TODO Auto-generated method stub
		Summoner summoner = RiotAPI.getSummonerByName(name);
	  	JSONArray data=new JSONArray();
	  

     
        	
        	List<VoteModel> movies_list = null;
        	db.createIndex("match_id", "match_id", null,
                    new IndexField[]{
                        new IndexField("match_id",SortOrder.asc)});
    		Long matchid=2812023211L;
    		//long matchid=listmatch.get(i).getID();
    		String name_uppercase= name.substring(0,1).toUpperCase()+name.substring(1);
    		movies_list = db.findByIndex("{selector: { to:"+name_uppercase+",confirmed:true}}",
    				VoteModel.class);
    		
    		
    		while(movies_list.size()!=0){
				VoteModel voteModel = movies_list.get(0);
				
					JSONArray vote_array=new JSONArray();
					JSONObject vote=new JSONObject();
					for (int k=0;k<movies_list.size();k++) {
						VoteModel voteModel_array = movies_list.get(k);
						if (voteModel_array.getMatch_id()==voteModel.getMatch_id()) {
							JSONObject data_vote=new JSONObject();
							data_vote.put("to", movies_list.get(k).getTo());
							data_vote.put("from", movies_list.get(k).getFrom());
							data_vote.put("data", movies_list.get(k).getData());
							data_vote.put("profile_icon", movies_list.get(k).getProfile_icon());
							data_vote.put("match_id", movies_list.get(k).getMatch_id());
							
							vote_array.put(data_vote);
							movies_list.remove(k);
						}
						
					}
					vote.put("match_id", voteModel.getMatch_id());
					
					vote.put("votes", vote_array);
				
					
		    		for (Participant users:RiotAPI.getMatch( voteModel.getMatch_id()).getParticipants()) {	        													
						
		    			if (name.contains(users.getSummonerName()) || name_uppercase.contains(users.getSummonerName())) {
		    				vote.put("champion", users.getChampion().toString());
		    				vote.put("profile_icon", users.getProfileIconID());
		    				vote.put("details", getMatch(voteModel.getMatch_id(),name).get(0));
						}
					
		    		}
		    		data.put(vote);
			}
    	
    		   	
    		
		
        
        System.out.println(data);
        
		return data;
	}

	@Override
	public JSONArray getLastVotes(String name ,Long matchid) {
		// TODO Auto-generated method stub
		Summoner summoner = RiotAPI.getSummonerByName(name);
	  	String data="[";
	  	List<MatchReference> listmatch=summoner.getMatchList();
        JSONArray last_match=new JSONArray();
        List<VoteModel> movies = null;
        
        	RiotAPI.getMatch(listmatch.get(1).getID());
        	List<VoteModel> movies_list = null;
        	
    		
    		//long matchid=listmatch.get(1).getID();
        	String name_uppercase= name.substring(0,1).toUpperCase()+name.substring(1);
    		movies_list = db.findByIndex("{selector: { match_id: "+matchid+",to:"+name_uppercase+"}}",
    				VoteModel.class);
    		 data+=movies_list+",";
		
        data=data.substring(0,data.length()-1);
        data=data+"]";
        System.out.println(data);
        JSONArray json_data_1=new JSONArray(data);
        JSONArray json_data=new JSONArray();
        json_data.put(json_data_1);
        json_data.put(getMatch(matchid));
        json_data.put(getMatch(matchid,name));
        System.out.println(json_data);
		return json_data;
	}

	@Override
	public JSONArray getVotesById(String string, Long string2) {
		// TODO Auto-generated method stub
		Summoner summoner = RiotAPI.getSummonerByName(string);
	  	String data="[";
	  	List<MatchReference> listmatch=summoner.getMatchList();
        JSONArray last_match=new JSONArray();
        List<VoteModel> movies = null;
        
        	RiotAPI.getMatch(string2);
        	List<VoteModel> movies_list = null;
        	
    		Long matchid=2812023211L;
    		//long matchid=listmatch.get(i).getID();
    		String name_uppercase= string.substring(0,1).toUpperCase()+string.substring(1);
    		movies_list = db.findByIndex("{selector: {match_id: "+string2+",to:"+name_uppercase+"}}",
    				VoteModel.class);
    		 data+=movies_list+",";
		
        data=data.substring(0,data.length()-1);
        data=data+"]";
        JSONArray json_data_1=new JSONArray(data);
        JSONArray json_data=new JSONArray();
        json_data.put(json_data_1);
        json_data.put(getMatch(string2));
        json_data.put(getMatch(string2,string));
        System.out.println(json_data);
		return json_data;
	}
	private JSONArray getMatch(Long matchid, String string) {
		// TODO Auto-generated method stub
		JSONArray history=new JSONArray();
		int counter=0;
		String name_uppercase= string.substring(0,1).toUpperCase()+string.substring(1);
		for (Participant users:RiotAPI.getMatch(matchid).getParticipants()) {
    			
				if (users.getSummonerName().contains(string) || name_uppercase.contains(users.getSummonerName())) {
					JSONObject stats=new JSONObject();
					
					if (counter<=5) {
						stats.put("win", RiotAPI.getMatch(matchid).getTeams().get(0).getWinner());
						
					}
					else {
						stats.put("win", RiotAPI.getMatch(matchid).getTeams().get(1).getWinner());
					}
					stats.put("champ_id", users.getChampionID());
					stats.put("name", users.getSummonerName());
					stats.put("champion", users.getChampion());					
					stats.put("assists", users.getStats().getAssists());
					stats.put("deaths", users.getStats().getDeaths());
					stats.put("kills", users.getStats().getKills());
					
					stats.put("item_0", users.getStats().getItem0ID());
					stats.put("item_1", users.getStats().getItem1ID());
					stats.put("item_2", users.getStats().getItem2ID());
					stats.put("item_3", users.getStats().getItem3ID());
					stats.put("item_4", users.getStats().getItem4ID());
					stats.put("item_5", users.getStats().getItem5ID());
				
					history.put(stats);
				}
				
			
		}
		return history;
	}

	public JSONArray getMatch(Long matchid) {
		// TODO Auto-generated method stub
		JSONArray history=new JSONArray();
		int counter=0;
		
		for (Participant users:RiotAPI.getMatch(matchid).getParticipants()) {
    																			
				JSONObject stats=new JSONObject();
				
				if (counter<=5) {
					stats.put("win", RiotAPI.getMatch(matchid).getTeams().get(0).getWinner());
					
				}
				else {
					stats.put("win", RiotAPI.getMatch(matchid).getTeams().get(1).getWinner());
				}
				stats.put("champ_id", users.getChampionID());
				stats.put("name", users.getSummonerName());
				stats.put("champion", users.getChampion());					
				stats.put("assists", users.getStats().getAssists());
				stats.put("deaths", users.getStats().getDeaths());
				stats.put("kills", users.getStats().getKills());
				
				stats.put("item_0", users.getStats().getItem0ID());
				stats.put("item_1", users.getStats().getItem1ID());
				stats.put("item_2", users.getStats().getItem2ID());
				stats.put("item_3", users.getStats().getItem3ID());
				stats.put("item_4", users.getStats().getItem4ID());
				stats.put("item_5", users.getStats().getItem5ID());
			
				history.put(stats);
			
		}
		return history;
	}
}
