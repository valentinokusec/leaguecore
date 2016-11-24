package com.leaguelove.services;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leaguelove.dao.MatchDao;
import com.leaguelove.domain.Match;
import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.QueueType;
import com.robrua.orianna.type.core.common.Season;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.core.game.Game;
import com.robrua.orianna.type.core.league.League;
import com.robrua.orianna.type.core.match.Participant;
import com.robrua.orianna.type.core.matchlist.MatchReference;
import com.robrua.orianna.type.core.staticdata.Champion;
import com.robrua.orianna.type.core.stats.ChampionStats;
import com.robrua.orianna.type.core.stats.PlayerStatsSummary;
import com.robrua.orianna.type.core.stats.PlayerStatsSummaryType;
import com.robrua.orianna.type.core.summoner.Summoner;

@Service
public class LiveStatsServiceImpl implements LiveStatsService {

	@Autowired
	MatchDao matchdao;

	@Override
	public JSONArray getLiveStats(String name) {

		Summoner summoner = RiotAPI.getSummonerByName(name);

		if (summoner.getCurrentGame() == null) {
			JSONArray ja = new JSONArray();

			for (CurrentGame iterable_element : RiotAPI.getFeaturedGames()) {
				Long min = iterable_element.getLength() / 60;
				Long sec = iterable_element.getLength() % 60;
				String sumName = iterable_element.getParticipants().iterator().next().getSummonerName();
				String tier;
				try {
					tier = RiotAPI.getLeagueEntriesBySummonerName(sumName).get(0).getTier() + " "
							+ RiotAPI.getLeagueEntriesBySummonerName(sumName).get(0).getEntries().iterator().next()
									.getDivision();
				} catch (Exception e) {
					tier = "Unranked";
				}
				ja.put(sumName);
				ja.put(tier);
				ja.put(min + ":" + sec);

			}
			return ja;
		} else {
			return getLiveStatsFromSummoner(summoner);
		}
	}

	private JSONArray getLiveStatsFromSummoner(Summoner summoner) {

		JSONArray liveStatsArray = new JSONArray();
		JSONObject liveStats = new JSONObject();
		liveStats.put("game_mode", summoner.getCurrentGame().getQueueType());

		JSONArray players = new JSONArray();
		for (com.robrua.orianna.type.core.currentgame.Participant player : summoner.getCurrentGame()
				.getParticipants()) {

			JSONObject playerStats = new JSONObject();
			playerStats.put("name", player.getSummonerName());
			playerStats.put("id", player.getSummonerID());
			playerStats.put("champion_name", player.getChampion().getName());
			Champion ch = RiotAPI.getChampionByName(player.getChampion().getName());
			playerStats.put("champion_key", ch.getKey());
			playerStats.put("icon", player.getProfileIconID());
			String tier;
			try {
				tier = RiotAPI.getLeagueEntriesBySummonerName(player.getSummonerName()).get(0).getTier() + " "
						+ RiotAPI.getLeagueEntriesBySummonerName(player.getSummonerName()).get(0).getEntries()
								.iterator().next().getDivision();
			} catch (Exception e) {
				tier = "Unranked";
			}
			playerStats.put("tier", tier);
			playerStats.put("general_stats", getChampionList(RiotAPI.getSummonerByName(player.getSummonerName()),
					player.getChampion().getName()));

			// playerStats.put("history",getHistory(RiotAPI.getSummonerByName(player.getSummonerName())));
			// playerStats.put("history_for_champion",getHistoryForChampion(summoner,player.getChampion().getName()));

			players.put(playerStats);
		}

		liveStats.put("players", players);

		JSONObject stats = getStats(players);

		liveStats.put("stats", stats);

		JSONArray generalInfo = getGeneralInfo(players);

		liveStats.put("general_stats", generalInfo);

		liveStatsArray.put(liveStats);

		return liveStatsArray;
	}

	private JSONArray getGeneralInfo(JSONArray players) {
		// TODO Auto-generated method stub
		JSONArray generalInfo = new JSONArray();
		for (int i = 0; i < players.length(); i++) {

			generalInfo.put(getUser(players, i));
		}

		return generalInfo;
	}

	private JSONObject getUser(JSONArray players, int i) {
		JSONObject user = new JSONObject();
		user.put("name", players.getJSONObject(i).getString("name"));
		user.put("tier", players.getJSONObject(i).getString("tier"));
		user.put("champion", players.getJSONObject(i).getString("champion_key"));
		user.put("champion_name", players.getJSONObject(i).getString("champion_name"));
		if (players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(0).getString("name").contains("all")) {
			user.put("general_info", getUserInfo(players, i, 0));
			user.put("champion_info", getUserInfo(players, i, 1));
		} else {
			user.put("general_info", getUserInfo(players, i, 1));
			user.put("champion_info", getUserInfo(players, i, 0));
		}

		return user;
	}

	private JSONArray getUserInfo(JSONArray players, int i, int info) {
		int counter = 10;
		int counter_assists = 10;
		int counter_deaths = 10;
		int counter_winrate = 10;
		Double winrate = 0d;
		Double kills = 0d;
		Double deaths = 0d;
		Double assists = 0d;
		try {
			kills = Double.parseDouble(players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info)
					.getString("kills_per_game").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (kills > Double.parseDouble(players.getJSONObject(k).getJSONArray("general_stats").getJSONObject(info).getString("kills_per_game").replace(",", "."))) {
					counter--;

				}
			}

		} catch (Exception e) {
			
		}
		try {
			assists = Double.parseDouble(players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info)
					.getString("assists_per_game").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (assists > Double.parseDouble(players.getJSONObject(k).getJSONArray("general_stats")
						.getJSONObject(info).getString("assists_per_game").replace(",", "."))) {
					counter_assists--;

				}

			}
		} catch (Exception e) {
			
		}
		try {
			deaths = Double.parseDouble(players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info)
					.getString("deaths_per_game").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (deaths > Double.parseDouble(players.getJSONObject(k).getJSONArray("general_stats")
						.getJSONObject(info).getString("deaths_per_game").replace(",", "."))) {
					counter_deaths--;

				}

			}
		} catch (Exception e) {
			
		}
		try {

			winrate = players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info).getDouble("winrate");

			for (int k = 0; k < players.length(); k++) {

				if (winrate > players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info)
						.getDouble("winrate")) {
					counter_winrate--;

				}

			}
		} catch (Exception e) {
			
		}
		JSONArray userInfo = new JSONArray();
		JSONObject killsInfo = new JSONObject();
		JSONObject assistsInfo = new JSONObject();
		JSONObject deathsInfo = new JSONObject();
		JSONObject winrateInfo = new JSONObject();

		killsInfo.put("kills_per_game", kills);
		killsInfo.put("kills", counter);
		killsInfo.put("label", "Kills");
		assistsInfo.put("assists_per_game", assists);
		assistsInfo.put("assists", counter_assists);
		assistsInfo.put("label", "Assists");
		deathsInfo.put("deaths_per_game", deaths);
		deathsInfo.put("deaths", counter_deaths);
		deathsInfo.put("label", "Assists");
		winrateInfo.put("winrate_c", counter_winrate);
		winrateInfo.put("winrate", winrate);
		winrateInfo.put("label", "winrate");
		userInfo.put(killsInfo);
		userInfo.put(assistsInfo);
		userInfo.put(deathsInfo);
		userInfo.put(winrateInfo);
		return userInfo;
	}

	private JSONObject getStats(JSONArray players) {
		JSONObject stats = new JSONObject();
		String highestKDAName = "";
		String highestKDAKey = "";
		Double highestKDA = players.getJSONObject(0).getJSONArray("general_stats").getJSONObject(0).getDouble("kda");
		String lowestKDAName = "";
		Double lowestKDA = players.getJSONObject(0).getJSONArray("general_stats").getJSONObject(0).getDouble("kda");
		String lowestKDAKey = "";
		for (int i = 0; i < players.length(); i++) {
			if (players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(0).getString("name")
					.contains("all")) {

				if (highestKDA < players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(0)
						.getDouble("kda")) {
					highestKDA = players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(0)
							.getDouble("kda");
					highestKDAName = players.getJSONObject(i).getString("name");
					highestKDAKey = players.getJSONObject(i).getString("champion_key");
				}
				if (lowestKDA > players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(0)
						.getDouble("kda")) {
					lowestKDA = players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(0)
							.getDouble("kda");
					lowestKDAName = players.getJSONObject(i).getString("name");
					lowestKDAKey = players.getJSONObject(i).getString("champion_key");
				}
			}
		}
		stats.put("highest_kda", highestKDA);
		stats.put("highest_kda_name", highestKDAName);
		stats.put("highest_kda_key", highestKDAKey);
		stats.put("lowest_kda", lowestKDA);
		stats.put("lowest_kda_name", lowestKDAName);
		stats.put("lowest_kda_key", lowestKDAKey);

		return stats;
	}

	private JSONArray getChampionList(Summoner summoner, String name) {
		// TODO Auto-generated method stub
		Map<Champion, ChampionStats> champions;
		Map<PlayerStatsSummaryType, PlayerStatsSummary> que;
		JSONArray championList = new JSONArray();
		JSONArray allChampionList = new JSONArray();
		try {
			boolean first_time = true;
			champions = summoner.getRankedStats(Season.SEASON2016);
			for (Map.Entry<Champion, ChampionStats> entry : champions.entrySet()) {
				JSONObject allChampion=new JSONObject();
				JSONObject champion = new JSONObject();
				if (entry.getValue().getChampion() == null) {
					champion= getStatsObject(entry,"all");
					allChampion= getStatsObject(entry,"all");
					championList.put(champion);
				} else if (entry.getValue().getChampion().toString().contains(name)) {
					first_time = false;
					champion= getStatsObject(entry,entry.getValue().getChampion().getName());
					allChampion= getStatsObject(entry,entry.getValue().getChampion().getName());
					championList.put(champion);
				}
				else
				{
					allChampion= getStatsObject(entry,entry.getValue().getChampion().getName());
				}
				
				
				allChampionList.put(allChampion);
				
			}
			JSONObject champion = new JSONObject();
			if (first_time) {
				champion.put("name", "First time");
				champion.put("games_played", 0);
				champion.put("kills", 0);
				champion.put("assists", 0);
				champion.put("deaths", 0);
				champion.put("loses", 0);
				champion.put("wins", 0);

				NumberFormat formatter = new DecimalFormat("#0.0");
				NumberFormat formatter_winrate = new DecimalFormat("#0.00");

				champion.put("kills_per_game", 0);
				champion.put("deaths_per_game", 0);
				champion.put("assists_per_game", 0);

				champion.put("winrate", 0);

				champion.put("loserate", 0);
				championList.put(champion);
			}
			
			championList.put(getSortedList(allChampionList));
		} catch (Exception e) {
			JSONObject champion1 = new JSONObject();
			que = summoner.getStats(Season.SEASON2016);
			for (Entry<PlayerStatsSummaryType, PlayerStatsSummary> entry : que.entrySet()) {

				JSONObject champion = new JSONObject();
				if (entry.getKey().toString().contains("Unranked")) {
					champion.put("name", "unranked");
					champion.put("games_played", entry.getValue().getAggregatedStats().getTotalGamesPlayed());
					champion.put("kills", round(entry.getValue().getAggregatedStats().getTotalKills(), 1));
					champion.put("assists", round(entry.getValue().getAggregatedStats().getTotalAssists(), 1));
					champion.put("deaths", round(entry.getValue().getAggregatedStats().getTotalDeaths(), 1));
					champion.put("loses", entry.getValue().getAggregatedStats().getTotalLosses());
					champion.put("wins", entry.getValue().getAggregatedStats().getTotalWins());

					NumberFormat formatter = new DecimalFormat("#0.0");
					NumberFormat formatter_winrate = new DecimalFormat("#0.00");

					Double killsPerGame = (double) (entry.getValue().getAggregatedStats().getTotalKills())
							/ (double) (entry.getValue().getAggregatedStats().getTotalGamesPlayed());
					Double deathsPerGame = (double) (entry.getValue().getAggregatedStats().getTotalDeaths())
							/ (double) (entry.getValue().getAggregatedStats().getTotalGamesPlayed());
					Double assistsPerGame = (double) (entry.getValue().getAggregatedStats().getTotalAssists())
							/ (double) (entry.getValue().getAggregatedStats().getTotalGamesPlayed());
					Double winrate = (double) (entry.getValue().getAggregatedStats().getTotalWins())
							/ (double) (entry.getValue().getAggregatedStats().getTotalGamesPlayed()) * 100;
					champion.put("kda", round((killsPerGame + assistsPerGame) / deathsPerGame, 2));

					champion.put("kills_per_game", formatter.format(killsPerGame));
					champion.put("deaths_per_game", formatter.format(deathsPerGame));
					champion.put("assists_per_game", formatter.format(assistsPerGame));

					champion.put("winrate", round(winrate, 2));
					Double loserate = 1 - winrate;
					champion.put("loserate", round(loserate, 2));
					championList.put(champion);
				}

			}
			champion1.put("name", "First time");
			champion1.put("games_played", 0);
			champion1.put("kills", 0);
			champion1.put("assists", 0);
			champion1.put("deaths", 0);
			champion1.put("loses", 0);
			champion1.put("wins", 0);

			champion1.put("kills_per_game", 0);
			champion1.put("deaths_per_game", 0);
			champion1.put("assists_per_game", 0);

			champion1.put("winrate", 0);

			champion1.put("loserate", 0);
			championList.put(champion1);
		}
		
		return championList;
	}

	private JSONArray getSortedList(JSONArray allChampionList) {
		JSONArray sortedJsonArray = new JSONArray();
	    List<JSONObject> jsonList = new ArrayList<JSONObject>();
	    for (int i = 0; i < allChampionList.length(); i++) {
	        jsonList.add(allChampionList.getJSONObject(i));
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
	    for (int i = 0; i < allChampionList.length(); i++) {
	        sortedJsonArray.put(jsonList.get(i));
	    }
	    JSONArray champion_sorted=new JSONArray();
		for (int i = sortedJsonArray.length()-1; i > sortedJsonArray.length()-5; i--) {
			champion_sorted.put(sortedJsonArray.get(i));
			if (i==0) {
				i=sortedJsonArray.length()-5;
			}
		}
		champion_sorted.remove(0);
		return champion_sorted;
	}

	private JSONObject getStatsObject(Entry<Champion, ChampionStats> entry, String string) {
		JSONObject champion=new JSONObject();
		champion.put("name",string);
		champion.put("games_played", entry.getValue().getStats().getTotalGamesPlayed());
		champion.put("kills", round(entry.getValue().getStats().getTotalKills(), 1));
		champion.put("assists", round(entry.getValue().getStats().getTotalAssists(), 1));
		champion.put("deaths", round(entry.getValue().getStats().getTotalDeaths(), 1));
		champion.put("loses", entry.getValue().getStats().getTotalLosses());
		champion.put("wins", entry.getValue().getStats().getTotalWins());
		NumberFormat formatter = new DecimalFormat("#0.0");
		NumberFormat formatter_winrate = new DecimalFormat("#0.00");

		Double killsPerGame = (double) (entry.getValue().getStats().getTotalKills())
				/ (double) (entry.getValue().getStats().getTotalGamesPlayed());
		Double deathsPerGame = (double) (entry.getValue().getStats().getTotalDeaths())
				/ (double) (entry.getValue().getStats().getTotalGamesPlayed());
		Double assistsPerGame = (double) (entry.getValue().getStats().getTotalAssists())
				/ (double) (entry.getValue().getStats().getTotalGamesPlayed());
		Double winrate = (double) (entry.getValue().getStats().getTotalWins())
				/ (double) (entry.getValue().getStats().getTotalGamesPlayed()) * 100;
		champion.put("kda", round((killsPerGame + assistsPerGame) / deathsPerGame, 2));

		champion.put("kills_per_game", formatter.format(killsPerGame));
		champion.put("deaths_per_game", formatter.format(deathsPerGame));
		champion.put("assists_per_game", formatter.format(assistsPerGame));

		champion.put("winrate", round(winrate, 2));
		Double loserate = 1 - winrate;
		champion.put("loserate", round(loserate, 2));
		return champion;
	}

	private JSONArray getHistory(Summoner summoner) {

		List<MatchReference> listmatch = summoner.getMatchList();
		JSONArray history = new JSONArray();
		for (int i = 0; i < 5; i++) {
			int counter = 0;
			for (Participant users : RiotAPI.getMatch(listmatch.get(i).getID()).getParticipants()) {
				counter++;
				if (users.getSummonerName().contains(summoner.getName())) {
					JSONObject stats = new JSONObject();

					if (counter <= 5) {
						stats.put("win", RiotAPI.getMatch(listmatch.get(i).getID()).getTeams().get(0).getWinner());

					} else {
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
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	@Override
	public JSONArray getHistory(String name) {
		// TODO Auto-generated method stub
		Summoner summoner = RiotAPI.getSummonerByName(name);
		List<Game> games = RiotAPI.getRecentGames(summoner);
		List<MatchReference> listmatch = summoner.getMatchList();
		JSONArray history = new JSONArray();
		for (int i = 0; i < 10; i++) {
			int counter = 0;
			try {

				JSONObject stats = new JSONObject();
				stats.put("win", games.get(i).getStats().getWin());

				stats.put("matchid", String.valueOf(listmatch.get(i).getID()));
				stats.put("champion", games.get(i).getChampion());
				stats.put("assists", games.get(i).getStats().getAssists());
				stats.put("deaths", games.get(i).getStats().getDeaths());
				stats.put("kills", games.get(i).getStats().getKills());

				stats.put("item_0", games.get(i).getStats().getItem0ID());
				stats.put("item_1", games.get(i).getStats().getItem1ID());
				stats.put("item_2", games.get(i).getStats().getItem2ID());
				stats.put("item_3", games.get(i).getStats().getItem3ID());
				stats.put("item_4", games.get(i).getStats().getItem4ID());
				stats.put("item_5", games.get(i).getStats().getItem5ID());

				history.put(stats);

			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return history;
	}
}
