package com.leaguelove.services;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.leaguelove.domain.ChampionMainStats;
import com.leaguelove.domain.GeneralStats;
import com.leaguelove.domain.SummonerProfile;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.Season;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.staticdata.Champion;
import com.robrua.orianna.type.core.stats.ChampionStats;
import com.robrua.orianna.type.core.summoner.Summoner;

import scala.annotation.meta.setter;

@Service
public class LiveStatsServiceImpl implements LiveStatsService{

	@Override
	public ArrayList<SummonerProfile> getLiveStats(String name) {
			    	
		    	Summoner summoner = RiotAPI.getSummonerByName(name);
		    	ArrayList<SummonerProfile> list = new ArrayList<SummonerProfile>();
		    	
		    	if (summoner.getCurrentGame()==null) {
		    		SummonerProfile sum=new SummonerProfile();
		    		
		    		sum.setName("game");
		    		list.add(sum);
		    	
		    		for (CurrentGame iterable_element : RiotAPI.getFeaturedGames()) {
		    			
		    		
		    			SummonerProfile sum1=new SummonerProfile();
			    		sum1.setName(iterable_element.getParticipants().iterator().next().getSummonerName());
			    		list.add(sum1);
					}
		 
		    		return list;
				}
		    	else
		    	{	
		    	    list=getLiveStatsFromSummoner(summoner);
		    		
		    		return list;
		    	}
			}

	private ArrayList<SummonerProfile> getLiveStatsFromSummoner(Summoner summoner) {
		
				ArrayList<SummonerProfile> list = new ArrayList<SummonerProfile>();
				
				
				
		    	
		    	
		    	for (com.robrua.orianna.type.core.currentgame.Participant player:summoner.getCurrentGame().getParticipants()) {
		    		
		    		SummonerProfile sum= new SummonerProfile();
		    		
		    		
		    		sum.setName(player.getSummonerName());
		    		
		    		sum.setChampion_name(player.getChampion().getName());
		    		
		    		getChampionList(RiotAPI.getSummonerByName(player.getSummonerName()),player.getChampion().getName(),sum);
		    		list.add(sum);
		    		
		    //		player_stats.put("history",getHistory(RiotAPI.getSummonerByName(player.getSummonerName())));
		    //		player_stats.put("history_for_champion",getHistoryForChampion(summoner,player.getChampion().getName()));
		    		
		    		
				}
		    	

		    	
				return list;
			}
	private void getChampionList(Summoner summoner, String name, SummonerProfile sum) {
		// TODO Auto-generated method stub
		Map<Champion,ChampionStats> champions=summoner.getRankedStats(Season.SEASON2016);
      
        for (Map.Entry<Champion,ChampionStats> entry : champions.entrySet())
        {
        	JSONObject champion=new JSONObject();
        	GeneralStats gs= new GeneralStats();
        	ChampionMainStats cs=new ChampionMainStats();
        	if (entry.getValue().getChampion()==null) {
        		
        		gs.setName("all");
        		
        		gs.setGames_played(entry.getValue().getStats().getTotalGamesPlayed());
        		gs.setKills(entry.getValue().getStats().getTotalKills());
        		gs.setAssists(entry.getValue().getStats().getTotalAssists());
        		gs.setDeaths(entry.getValue().getStats().getTotalDeaths());
        		gs.setLoses(entry.getValue().getStats().getTotalLosses());
        		gs.setWins(entry.getValue().getStats().getTotalWins());
            	
            	NumberFormat formatter = new DecimalFormat("#0.0"); 
            	NumberFormat formatter_winrate = new DecimalFormat("#0.00");
            	
            	Double kills_per_game=(double) (entry.getValue().getStats().getTotalKills())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
            	Double deaths_per_game=(double) (entry.getValue().getStats().getTotalDeaths())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
            	Double assists_per_game=(double) (entry.getValue().getStats().getTotalAssists())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
            	Double winrate=(double) (entry.getValue().getStats().getTotalWins())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
            	
            	gs.setKills_per_game(formatter.format(kills_per_game));
            	gs.setDeaths_per_game(formatter.format(deaths_per_game));
            	gs.setAssists_per_game(formatter.format(assists_per_game));
            	
            	gs.setWinrate(round(winrate, 2));
            	Double loserate=1-winrate;
            	gs.setLoserate(loserate);
            	sum.setGeneral_stats(gs);
			}        	
        	
        	else if(entry.getValue().getChampion().toString().contains(name)) {
        		cs.setName("all");
        		
        		cs.setGames_played(entry.getValue().getStats().getTotalGamesPlayed());
        		cs.setKills(entry.getValue().getStats().getTotalKills());
        		cs.setAssists(entry.getValue().getStats().getTotalAssists());
        		cs.setDeaths(entry.getValue().getStats().getTotalDeaths());
        		cs.setLoses(entry.getValue().getStats().getTotalLosses());
        		cs.setWins(entry.getValue().getStats().getTotalWins());
            	
            	NumberFormat formatter = new DecimalFormat("#0.0"); 
            	NumberFormat formatter_winrate = new DecimalFormat("#0.00");
            	
            	Double kills_per_game=(double) (entry.getValue().getStats().getTotalKills())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
            	Double deaths_per_game=(double) (entry.getValue().getStats().getTotalDeaths())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
            	Double assists_per_game=(double) (entry.getValue().getStats().getTotalAssists())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
            	Double winrate=(double) (entry.getValue().getStats().getTotalWins())/(double)(entry.getValue().getStats().getTotalGamesPlayed());
            	
            	cs.setKills_per_game(formatter.format(kills_per_game));
            	cs.setDeaths_per_game(formatter.format(deaths_per_game));
            	cs.setAssists_per_game(formatter.format(assists_per_game));
            	
            	cs.setWinrate(winrate);
            	Double loserate=1-winrate;
            	cs.setLoserate(loserate);
            	sum.setChampion_stats(cs);
			}
        	
        	
        	
    
        	
        	
          
        }
   
    	
		
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
