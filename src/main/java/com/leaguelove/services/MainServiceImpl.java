package com.leaguelove.services;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
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
import com.robrua.orianna.type.core.common.Season;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.staticdata.Champion;
import com.robrua.orianna.type.core.stats.ChampionStats;
import com.robrua.orianna.type.core.summoner.Summoner;

@Service
public class MainServiceImpl implements MainService{

	
	CloudantClient client;
	Database db;
	
	private static final String API_KEY="172d9054-b070-449d-bb68-cbbe94f29e7c";
	
    public MainServiceImpl()
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
	public JSONArray getGeneralData(String name) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				JSONArray general_stats_array =new JSONArray();
		    	JSONObject general_stats =new JSONObject();
		    	
		    	
		        
		        Summoner summoner = RiotAPI.getSummonerByName(name);
		        
		        
		 //       general_stats.put("champion_history", getHistoryForChampion(summoner,"Fiora"));
		        general_stats.put("league", summoner.getLeagueEntries().iterator().next().getTier()+" "+summoner.getLeagueEntries().iterator().next().getEntries().iterator().next().getDivision());
		        general_stats.put("name", summoner.getName());
		        general_stats.put("profile_icon", summoner.getProfileIconID());
		        general_stats.put("history", getHistory(summoner));
		        general_stats.put("champion_list", getChampionList(summoner));
		        int wincounter=0;
		        for (int i = 0; i < general_stats.getJSONArray("history").length(); i++) {
		        	if (general_stats.getJSONArray("history").getJSONObject(i).getBoolean("win")==true) {
		        		wincounter++;
					}
		        	
					
				}
		        NumberFormat formatter_winrate = new DecimalFormat("#0.00");
		        Double recent_winrate=(double)(wincounter)/(double)(10);
		        Double recent_loserate=1-recent_winrate;
		        general_stats.put("recent_winrate", recent_winrate);
		        general_stats.put("recent_loserate", recent_loserate);
		        general_stats_array.put(general_stats);
		        
		        return general_stats_array;
	}

	@Override
	public JSONArray getRecentHistory(String name) {
		// TODO Auto-generated method stub
		 Summoner summoner = RiotAPI.getSummonerByName(name);
	    	List<MatchReference> listmatch=summoner.getMatchList();
	        JSONArray history=new JSONArray();
	        int counter=0;
	        int winrate=0;
	        int kills=0;
	        int assists=0;
	        int deaths=0;
	    	List<String> champ_list = new ArrayList<String>();
			List<Long> item_list = new ArrayList<Long>();
			
	        for (int i = 0; i < 10; i++) {
	        	
	        	for (Participant users:RiotAPI.getMatch(listmatch.get(i).getID()).getParticipants()) {
	        		
					if (users.getSummonerName().contains(summoner.getName())) {
						
							
							counter++;
						
						
						if (counter<=5) {
							
							if (RiotAPI.getMatch(listmatch.get(i).getID()).getTeams().get(0).getWinner()==true) {
								winrate++;
							}
						}
						else {
						
							if (RiotAPI.getMatch(listmatch.get(i).getID()).getTeams().get(0).getWinner()==true) {
								winrate++;
							}
						}
						
						assists+=users.getStats().getAssists();
						deaths+=users.getStats().getDeaths();
						kills+=users.getStats().getKills();
						
						item_list.add(users.getStats().getItem0ID());
						item_list.add(users.getStats().getItem1ID());
						item_list.add(users.getStats().getItem2ID());
						item_list.add(users.getStats().getItem3ID());
						item_list.add(users.getStats().getItem4ID());
						item_list.add(users.getStats().getItem5ID());
						
						champ_list.add(users.getChampion().toString());
						}
					}
					
				}
	        String most_played="";
	        int played=0;
	        for (int i = 0; i < champ_list.size(); i++) {
				if ( Collections.frequency(champ_list, champ_list.get(i))>played) {
					most_played=champ_list.get(i);
					played=Collections.frequency(champ_list, champ_list.get(i));
				}
			}
	        
	        Long most_used_item=0L;
	        int played_item=0;
	        for (int i = 0; i < item_list.size(); i++) {
				if ( Collections.frequency(item_list, item_list.get(i))>played_item) {
					if (item_list.get(i)!=0) {
						most_used_item=item_list.get(i);
						played_item=Collections.frequency(item_list, item_list.get(i));
					}
					
				}
			}
	        
	        JSONObject stats=new JSONObject();
	        stats.put("most_played", most_played);
	        stats.put("most_played_item", most_used_item);
	        stats.put("kills", kills);
	        stats.put("deaths", deaths);
	        stats.put("assists", assists);
	        Double kills_per=(double)(kills)/(double)(10);
	      
	        stats.put("kills_per", kills_per);
	        Double assists_per=(double)(assists)/(double)(10);
	       
	        stats.put("assists_per", assists_per);
	        Double deaths_per=(double)(deaths)/(double)(10);
	     
	        stats.put("deaths_per", deaths_per);
	        Double recent_winrate=(double)(winrate)/(double)(10);
	       
	        stats.put("recent_winrate", recent_winrate);
	        
	        history.put(stats);
	        
			return history;
	}

	@Override
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

	@Override
	public JSONArray getGeneralHistory(String name,int a, String champion) {
		
		Summoner summoner = RiotAPI.getSummonerByName(name);
		List<MatchReference> listmatch=summoner.getMatchList();
        JSONArray history=new JSONArray();
        
        int counte_champr=0;
        for (int i=4*a-4; i < 100; i++) {
        	int counter=0;
        	for (Participant users:RiotAPI.getMatch(listmatch.get(i).getID()).getParticipants()) {
        		counter++;
				if (users.getSummonerName().contains(summoner.getName())) {
					if (users.getChampion().toString().contains(champion) || champion.contains("all")) {
					counte_champr++;	
					
					JSONObject stats=new JSONObject();
					
					if (counter<=5) {
						stats.put("win", RiotAPI.getMatch(listmatch.get(i).getID()).getTeams().get(0).getWinner());
						
					}
					else {
						stats.put("win", RiotAPI.getMatch(listmatch.get(i).getID()).getTeams().get(1).getWinner());
					}
					
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
				if (counte_champr==4) {
					i=101;
				}
			}
			
		}
		return history;
		
	}

	@Override
	public JSONArray getChampionHistory(String name, String championname) {
		// TODO Auto-generated method stub
	  	System.out.println(championname);
    	Summoner summoner = RiotAPI.getSummonerByName(name);
    	List<MatchReference> listmatch=summoner.getMatchList();
        JSONArray history=new JSONArray();
        int counter=0;
        for (int i = 0; i < 100; i++) {
        	
        	for (Participant users:RiotAPI.getMatch(listmatch.get(i).getID()).getParticipants()) {
        		
				if (users.getSummonerName().contains(summoner.getName())) {
					if (users.getChampion().toString().contains(championname)) {
						
						counter++;
					JSONObject stats=new JSONObject();
					
					if (counter<=5) {
						stats.put("win", RiotAPI.getMatch(listmatch.get(i).getID()).getTeams().get(0).getWinner());
						
					}
					else {
						stats.put("win", RiotAPI.getMatch(listmatch.get(i).getID()).getTeams().get(1).getWinner());
					}
					stats.put("matchid",String.valueOf(listmatch.get(i).getID()));	
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
				if (counter==5) {
					i=101;
				}
			}
			
		}
		return history;
	}
private JSONArray getHistory(Summoner summoner) {
		
		List<MatchReference> listmatch=summoner.getMatchList();
        JSONArray history=new JSONArray();
        for (int i = 0; i < 10; i++) {
        	int counter=0;
        	for (Participant users:RiotAPI.getMatch(listmatch.get(i).getID()).getParticipants()) {
        		counter++;
				if (users.getSummonerName().contains(summoner.getName())) {
					JSONObject stats=new JSONObject();
					
					if (counter<=5) {
						stats.put("win", RiotAPI.getMatch(listmatch.get(i).getID()).getTeams().get(0).getWinner());
						
					}
					else {
						stats.put("win", RiotAPI.getMatch(listmatch.get(i).getID()).getTeams().get(1).getWinner());
					}
					
					stats.put("matchid", String.valueOf(listmatch.get(i).getID()));			
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
			
		}
		return history;
	}
private JSONArray getChampionList(Summoner summoner) {
	
	Map<Champion,ChampionStats> champions=summoner.getRankedStats(Season.SEASON2016);
    JSONArray champions_list=new JSONArray();
    for (Map.Entry<Champion,ChampionStats> entry : champions.entrySet())
    {
    	JSONObject champion=new JSONObject();
    	if (entry.getValue().getChampion()==null) {
    		champion.put("name","all");
		}
    	else {
    		champion.put("name",entry.getValue().getChampion());
		}
    	
    	champion.put("games_played", entry.getValue().getStats().getTotalGamesPlayed());
    	champion.put("kills", entry.getValue().getStats().getTotalKills());
    	champion.put("assists", entry.getValue().getStats().getTotalAssists());
    	champion.put("deaths", entry.getValue().getStats().getTotalDeaths());
    	champion.put("loses", entry.getValue().getStats().getTotalLosses());
    	champion.put("wins", entry.getValue().getStats().getTotalWins());
    	
    	NumberFormat formatter = new DecimalFormat("#0.0"); 
    	NumberFormat formatter_winrate = new DecimalFormat("#0.00");
    	
    	Double kills_per_game=(double) (entry.getValue().getStats().getTotalKills())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
    	Double deaths_per_game=(double) (entry.getValue().getStats().getTotalDeaths())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
    	Double assists_per_game=(double) (entry.getValue().getStats().getTotalAssists())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
    	Double winrate=(double) (entry.getValue().getStats().getTotalWins())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
    	
    	champion.put("kills_per_game", formatter.format(kills_per_game));
    	champion.put("deaths_per_game", formatter.format(deaths_per_game));
    	champion.put("assists_per_game", formatter.format(assists_per_game));
    	
    	champion.put("winrate", winrate);
    	Double loserate=1-winrate;
    	champion.put("loserate", loserate);
    	

    	
    	champions_list.put(champion);
      
    }
    
    JSONArray sortedJsonArray = new JSONArray();
    List<JSONObject> jsonList = new ArrayList<JSONObject>();
    for (int i = 0; i < champions_list.length(); i++) {
        jsonList.add(champions_list.getJSONObject(i));
    }
    Collections.sort(jsonList, new Comparator<JSONObject>() {

        public int compare(JSONObject a, JSONObject b) {
        	Integer valA = 0 ;
            Integer valB = 0 ;

            try {
                valA = (Integer) a.get("games_played");
                valB = (Integer) b.get("games_played");
            } 
            catch (JSONException e) {
                //do something
            }

            return valA.compareTo(valB);
        }
    });
    for (int i = 0; i < champions_list.length(); i++) {
        sortedJsonArray.put(jsonList.get(i));
    }
    JSONArray champion_sorted=new JSONArray();
	for (int i = sortedJsonArray.length()-1; i > sortedJsonArray.length()-7; i--) {
		champion_sorted.put(sortedJsonArray.get(i));
	}
	
	return champion_sorted;
}

@Override
public JSONArray getRandomMessage() {
	// TODO Auto-generated method stub
	JSONArray data=new JSONArray();
	List<VoteModel> movies_list = null;
	
	
	//long matchid=listmatch.get(i).getID();
	
	movies_list = db.findByIndex("{selector: {confirmed:true}}",
			VoteModel.class);


    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = new Random().nextInt((movies_list.size() - 0) + 1) + 0;
	int k=randomNum;
	JSONObject data_vote=new JSONObject();
	data_vote.put("to", movies_list.get(k).getTo());
	data_vote.put("from", movies_list.get(k).getFrom());
	data_vote.put("data", movies_list.get(k).getData());
	data_vote.put("profile_icon", movies_list.get(k).getProfile_icon());
	data_vote.put("match_id", movies_list.get(k).getMatch_id());
	data.put(data_vote);
	data.put(getMatchRandom(movies_list.get(k).getMatch_id(), movies_list.get(k).getTo()));
	return data;
}
private JSONArray getMatchRandom(Long matchid, String string) {
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

@Override
public String CheckSummonerName(String name) {
	// TODO Auto-generated method stub
	try {
		Summoner summoner = RiotAPI.getSummonerByName(name);
		return "ok";
	} catch (Exception e) {
		return "nope";
	}
	
}
}
