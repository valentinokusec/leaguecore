package com.leaguelove.services;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Season;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.staticdata.Champion;
import com.robrua.orianna.type.core.stats.ChampionStats;
import com.robrua.orianna.type.core.summoner.Summoner;

@Service
public class LiveStatsServiceImpl implements LiveStatsService{

	@Override
	public JSONArray getLiveStats(String name) {
			    	
		    	Summoner summoner = RiotAPI.getSummonerByName(name);
		    	
		    	if (summoner.getCurrentGame()==null) {
		    		JSONArray ja=new JSONArray();
				    ja.put("game");
					for (CurrentGame iterable_element : RiotAPI.getFeaturedGames()) {
		    			
						ja.put(iterable_element.getParticipants().iterator().next().getSummonerName());
		    			
					}
					return ja;
				}
		    	else
		    	{
		    		return getLiveStatsFromSummoner(summoner);
		    	}
			}

	private JSONArray getLiveStatsFromSummoner(Summoner summoner) {
				
				JSONArray live_stats_array=new JSONArray();
				JSONObject live_stats=new JSONObject();
				live_stats.put("game_mode", summoner.getCurrentGame().getQueueType());
		    	
		    	JSONArray players=new JSONArray();
		    	for (com.robrua.orianna.type.core.currentgame.Participant player:summoner.getCurrentGame().getParticipants()) {
		    		
		    		JSONObject player_stats=new JSONObject();
		    		player_stats.put("name", player.getSummonerName());
		    		player_stats.put("champion_name", player.getChampion().getName());
		    		
		    		
		    		player_stats.put("general_stats",getChampionList(RiotAPI.getSummonerByName(player.getSummonerName()),player.getChampion().getName()));
		    		
		    //		player_stats.put("history",getHistory(RiotAPI.getSummonerByName(player.getSummonerName())));
		    //		player_stats.put("history_for_champion",getHistoryForChampion(summoner,player.getChampion().getName()));
		    		
		    		players.put(player_stats);
				}
		    	
		    	live_stats.put("players", players);
		    	live_stats_array.put(live_stats);
		    	
				return live_stats_array;
			}
	private JSONArray getChampionList(Summoner summoner, String name) {
		// TODO Auto-generated method stub
		Map<Champion,ChampionStats> champions=summoner.getRankedStats(Season.SEASON2016);
        JSONArray champions_list=new JSONArray();
        for (Map.Entry<Champion,ChampionStats> entry : champions.entrySet())
        {
        	JSONObject champion=new JSONObject();
        	if (entry.getValue().getChampion()==null) {
        		champion.put("name","all");
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
            	
            	champion.put("winrate", round(winrate,2));
            	Double loserate=1-winrate;
            	champion.put("loserate", round(loserate,2));
            	champions_list.put(champion);
			}
        	else if(entry.getValue().getChampion().toString().contains(name)) {
        		champion.put("name",entry.getValue().getChampion());
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
            	
            	champion.put("winrate", round(winrate,2));
            	Double loserate=1-winrate;
            	champion.put("loserate", round(loserate,2));
            	champions_list.put(champion);
			}
        	
        	
        	
    
        	
        	
          
        }
   
    	
		return champions_list;
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
		public static double round(double value, int places) {
		    if (places < 0) throw new IllegalArgumentException();

		    long factor = (long) Math.pow(10, places);
		    value = value * factor;
		    long tmp = Math.round(value);
		    return (double) tmp / factor;
		}
}
