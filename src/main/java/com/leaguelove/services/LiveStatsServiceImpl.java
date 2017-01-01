package com.leaguelove.services;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
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



import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.type.core.common.QueueType;
import com.robrua.orianna.type.core.common.Season;
import com.robrua.orianna.type.core.currentgame.CurrentGame;
import com.robrua.orianna.type.core.currentgame.MasteryRank;
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


	
	private JSONArray statsLive=new JSONArray();

	@Override
	public JSONArray getLiveStats(String name) {
		Summoner summoner = null;
		try
		{
		 summoner = RiotAPI.getSummonerByName(name);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		if (summoner == null) {
			
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
		}
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
		liveStats.put("game_mode", summoner.getCurrentGame().getMode());

		JSONArray players = new JSONArray();
		for (com.robrua.orianna.type.core.currentgame.Participant player : summoner.getCurrentGame()
				.getParticipants()) {

			JSONObject playerStats = new JSONObject();
			
			playerStats.put("name", player.getSummonerName());
			if (player.getSummonerName().contains(summoner.getName())) {
				playerStats.put("owner_team", player.getTeam());
				playerStats.put("owner", true);
			}
			else{
				playerStats.put("owner", false);
			}
			
			playerStats.put("team", player.getTeam().toString());
			playerStats.put("id", player.getSummonerID());
			for (MasteryRank   mastery:player.getMasteries()) {
				Long mastId=mastery.getMastery().getID();
				if (mastId==6161 || mastId==6162 || mastId==6141 || mastId==6361 || mastId==6362 || mastId==6363 || mastId==6261 || mastId==6262 || mastId==6263 ) {
					playerStats.put("keystone_img", mastery.getMastery().getImage().getFull());
				}
				
			}
			
			playerStats.put("rune1", player.getSummonerSpell1().toString());
			playerStats.put("rune2", player.getSummonerSpell2().toString());
			playerStats.put("rune1_img", player.getSummonerSpell1().getImage().getFull());
			playerStats.put("rune2_img", player.getSummonerSpell2().getImage().getFull());
			playerStats.put("champion_name", player.getChampion().getName());
			Champion ch = RiotAPI.getChampionByName(player.getChampion().getName());
			playerStats.put("champion_key", ch.getKey());
			playerStats.put("icon", player.getProfileIconID());
			String tier;
			String tier_img;
			try {
				tier = RiotAPI.getLeagueEntriesBySummonerName(player.getSummonerName()).get(0).getTier() + " "
						+ RiotAPI.getLeagueEntriesBySummonerName(player.getSummonerName()).get(0).getEntries()
								.iterator().next().getDivision();
				tier_img=tier.replace(" ", "_");
			} catch (Exception e) {
				tier = "Unranked";
				tier_img="Unranked";
			}
			playerStats.put("tier", tier);
			playerStats.put("tier_img", tier_img);
			playerStats.put("general_stats", getChampionList(RiotAPI.getSummonerByName(player.getSummonerName()),
					player.getChampion().getName()));
			

			// playerStats.put("history",getHistory(RiotAPI.getSummonerByName(player.getSummonerName())));
			// playerStats.put("history_for_champion",getHistoryForChampion(summoner,player.getChampion().getName()));

			players.put(playerStats);
		}
		
		
		if (summoner.getCurrentGame().getMode().toString().contains("CLASSIC")) {
		players=newList(players);
		
		JSONArray roles = getRolesStats(players);

		liveStats.put("roles", roles);
		}
		liveStats.put("players", players);

		JSONObject stats = getStats(players);

		liveStats.put("stats", stats);
		JSONArray generalInfo = getGeneralInfo(players);

		liveStats.put("general_stats", generalInfo);
		
		JSONArray metrics = getMetrics(generalInfo);

		liveStats.put("metrics", metrics);

		liveStatsArray.put(liveStats);
		
		statsLive=liveStatsArray;
		
		return liveStatsArray;
	}

	

	private JSONArray getMetrics(JSONArray players) {
		// TODO Auto-generated method stub
		
		JSONArray returnData= new JSONArray();
		
		JSONObject kdaGeneralArray= new JSONObject();
		JSONObject killsGeneralArray= new JSONObject();
		JSONObject deathsGeneralArray= new JSONObject();
		JSONObject assistsGeneralArray= new JSONObject();
		JSONObject winrateGeneralArray= new JSONObject();
		JSONObject gamesGeneralArray= new JSONObject();
		
		JSONObject kdaChampionArray= new JSONObject();
		JSONObject killsChampionArray= new JSONObject();
		JSONObject deathsChampionArray= new JSONObject();
		JSONObject assistsChampionArray= new JSONObject();
		JSONObject winrateChampionArray= new JSONObject();
		JSONObject gamesChampionArray= new JSONObject();
	
		
		
		
		List<JSONArray> kdaGeneral=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> killsGeneral=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> deathsGeneral=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> assistsGeneral=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> winrateGeneral=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> gamesGeneral=  Arrays.asList(new JSONArray[10]);
		
		List<JSONArray> kdaChampion=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> killsChampion=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> deathsChampion=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> assistsChampion=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> winrateChampion=  Arrays.asList(new JSONArray[10]);
		List<JSONArray> gamesChampion=  Arrays.asList(new JSONArray[10]);
		for (int i = 0; i < 10; i++) {
			kdaGeneral.set(i,new JSONArray());
			kdaChampion.set(i,new JSONArray());
			
			killsGeneral.set(i,new JSONArray());
			killsChampion.set(i,new JSONArray());
			
			deathsGeneral.set(i,new JSONArray());
			deathsChampion.set(i,new JSONArray());
			
			gamesGeneral.set(i,new JSONArray());
			gamesChampion.set(i,new JSONArray());
			
			winrateGeneral.set(i,new JSONArray());
			winrateChampion.set(i,new JSONArray());
			
			assistsGeneral.set(i,new JSONArray());
			assistsChampion.set(i,new JSONArray());
			
		}
		for (int i = 0; i < 10; i++) {
//			kdaGeneral.set(index, element)
//			players.getJSONObject(i).getJSONArray("general_info").
		
				
			
			JSONObject info=new JSONObject();
			JSONObject infoChampion=new JSONObject();
			info.put("champion", players.getJSONObject(i).getString("champion"));
			info.put("value", players.getJSONObject(i).getJSONArray("general_info").getJSONObject(0).getInt("stat"));
			
			
			gamesGeneral.get(players.getJSONObject(i).getJSONArray("general_info").getJSONObject(0).getInt("counter")).put(info);
			infoChampion.put("champion", players.getJSONObject(i).getString("champion"));
			infoChampion.put("value", players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(0).getInt("stat"));
			gamesChampion.get(players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(0).getInt("counter")).put(infoChampion);
			
			JSONObject kda=new JSONObject();
			JSONObject kdaInfo=new JSONObject();
			kda.put("champion", players.getJSONObject(i).getString("champion"));
			kda.put("value", players.getJSONObject(i).getJSONArray("general_info").getJSONObject(1).getInt("stat"));
			kdaGeneral.get(players.getJSONObject(i).getJSONArray("general_info").getJSONObject(1).getInt("counter")).put(kda);
			kdaInfo.put("champion", players.getJSONObject(i).getString("champion"));
			kdaInfo.put("value", players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(1).getInt("stat"));
			kdaChampion.get(players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(1).getInt("counter")).put(kdaInfo);
			
			JSONObject kills=new JSONObject();
			JSONObject infoKills=new JSONObject();
			kills.put("champion", players.getJSONObject(i).getString("champion"));
			kills.put("value", players.getJSONObject(i).getJSONArray("general_info").getJSONObject(2).getInt("stat"));
			killsGeneral.get(players.getJSONObject(i).getJSONArray("general_info").getJSONObject(2).getInt("counter")).put(kills);
			infoKills.put("champion", players.getJSONObject(i).getString("champion"));
			infoKills.put("value", players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(2).getInt("stat"));
			killsChampion.get(players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(2).getInt("counter")).put(infoKills);
			
			JSONObject assists=new JSONObject();
			JSONObject infoAssits=new JSONObject();
			assists.put("champion", players.getJSONObject(i).getString("champion"));
			assists.put("value", players.getJSONObject(i).getJSONArray("general_info").getJSONObject(3).getInt("stat"));
			assistsGeneral.get(players.getJSONObject(i).getJSONArray("general_info").getJSONObject(3).getInt("counter")).put(assists);
			infoAssits.put("champion", players.getJSONObject(i).getString("champion"));
			infoAssits.put("value", players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(3).getInt("stat"));
			assistsChampion.get(players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(3).getInt("counter")).put(infoAssits);
			
			JSONObject deaths=new JSONObject();
			JSONObject infoDeaths=new JSONObject();
			deaths.put("champion", players.getJSONObject(i).getString("champion"));
			deaths.put("value", players.getJSONObject(i).getJSONArray("general_info").getJSONObject(4).getInt("stat"));
			deathsGeneral.get(players.getJSONObject(i).getJSONArray("general_info").getJSONObject(4).getInt("counter")).put(deaths);
			infoDeaths.put("champion", players.getJSONObject(i).getString("champion"));
			infoDeaths.put("value", players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(4).getInt("stat"));
			deathsChampion.get(players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(4).getInt("counter")).put(infoDeaths);
			
			JSONObject winrate=new JSONObject();
			JSONObject infowinrate=new JSONObject();
			winrate.put("champion", players.getJSONObject(i).getString("champion"));
			winrate.put("value", players.getJSONObject(i).getJSONArray("general_info").getJSONObject(5).getInt("stat"));
			winrateGeneral.get(players.getJSONObject(i).getJSONArray("general_info").getJSONObject(5).getInt("counter")).put(winrate);
			infowinrate.put("champion", players.getJSONObject(i).getString("champion"));
			infowinrate.put("value", players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(5).getInt("stat"));
			winrateChampion.get(players.getJSONObject(i).getJSONArray("champion_info").getJSONObject(5).getInt("counter")).put(infowinrate);
			
			
		}
		gamesGeneralArray.put("label", "Games played");
		gamesGeneralArray.put("data", gamesGeneral);
		returnData.put(gamesGeneralArray);
		
		kdaGeneralArray.put("label", "KDA");
		kdaGeneralArray.put("data", kdaGeneral);
		returnData.put(kdaGeneralArray);
		
		killsGeneralArray.put("label", "Total kills");
		killsGeneralArray.put("data", killsGeneral);
		returnData.put(killsGeneralArray);
		
		deathsGeneralArray.put("label", "Total deaths");
		deathsGeneralArray.put("data", deathsGeneral);
		returnData.put(deathsGeneralArray);
		
		killsGeneralArray.put("label", "Total assists");
		killsGeneralArray.put("data", killsGeneral);
		returnData.put(killsGeneralArray);
		
		winrateGeneralArray.put("label", "Winrate");
		winrateGeneralArray.put("data", winrateGeneral);
		returnData.put(winrateGeneralArray);
		
		gamesChampionArray.put("label", "Games played(Champion)");
		gamesChampionArray.put("data", gamesChampion);
		returnData.put(gamesChampionArray);
		
		kdaChampionArray.put("label", "KDA(Champion)");
		kdaChampionArray.put("data", kdaChampion);
		returnData.put(kdaChampionArray);
		
		killsChampionArray.put("label", "Total kills(Champion)");
		killsChampionArray.put("data", killsChampion);
		returnData.put(killsChampionArray);
		
		deathsChampionArray.put("label", "Total deaths(Champion)");
		deathsChampionArray.put("data", deathsChampion);
		returnData.put(deathsChampionArray);
		
		assistsChampionArray.put("label", "Total assists");
		assistsChampionArray.put("data", assistsChampion);
		returnData.put(assistsChampionArray);
		
		winrateChampionArray.put("label", "Winrate(Champion)");
		winrateChampionArray.put("data", winrateChampion);
		returnData.put(winrateChampionArray);
		
	
		
		
		return returnData;
	}

	private JSONArray getRolesStats(JSONArray players) {
		JSONObject roles=new JSONObject();
		JSONArray rolesArray=new JSONArray();
		String ownerTeam="";
		for (int i = 0; i < 10; i++) {
			if (players.getJSONObject(i).getBoolean("owner")) {
				ownerTeam=players.getJSONObject(i).getString("team");
			}
		}
		for (int i = 0; i < 5; i++) {
			JSONObject users=new JSONObject();
			users.put("user_1", players.getJSONObject(i).getString("champion_key"));
			users.put("user_2", players.getJSONObject(i+5).getString("champion_key"));
			
			JSONArray generalStats=new JSONArray();
			for (int j = 0; j < 2; j++) {
				
			
			if (players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getInt("games_played")>players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getInt("games_played")) {
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getInt("games_played"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getInt("games_played"));
				if (players.getJSONObject(i).getString("team").contains(ownerTeam)) {
					data.put("higher", "true_true");
				}
				else
				{
					data.put("higher", "true_false");
				}
				generalStats.put(data);
			}
			else
			{
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getInt("games_played"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getInt("games_played"));
				if (players.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
					data.put("higher", "false_true");
				}
				else
				{
					data.put("higher", "false_false");
				}
				
				generalStats.put(data);
			}
			if (players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getDouble("kda")>players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getDouble("kda")) {
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getDouble("kda"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getDouble("kda"));
				if (players.getJSONObject(i).getString("team").contains(ownerTeam)) {
					data.put("higher", "true_true");
				}
				else
				{
					data.put("higher", "true_false");
				}
				generalStats.put(data);
			}
			else
			{
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getDouble("kda"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getDouble("kda"));
				if (players.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
					data.put("higher", "false_true");
				}
				else
				{
					data.put("higher", "false_false");
				}
				generalStats.put(data);
			}
			if (Double.parseDouble(players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getString("kills_per_game").replace(",", "."))>Double.parseDouble(players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getString("kills_per_game").replace(",", "."))) {
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getString("kills_per_game"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getString("kills_per_game"));
				if (players.getJSONObject(i).getString("team").contains(ownerTeam)) {
					data.put("higher", "true_true");
				}
				else
				{
					data.put("higher", "true_false");
				}
				generalStats.put(data);
			}
			else
			{
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getString("kills_per_game"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getString("kills_per_game"));
				if (players.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
					data.put("higher", "false_true");
				}
				else
				{
					data.put("higher", "false_false");
				}
				generalStats.put(data);
			}
			if (Double.parseDouble(players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getString("deaths_per_game").replace(",", "."))>Double.parseDouble(players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getString("deaths_per_game").replace(",", "."))) {
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getString("deaths_per_game"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getString("deaths_per_game"));
				if (players.getJSONObject(i).getString("team").contains(ownerTeam)) {
					data.put("higher", "true_true");
				}
				else
				{
					data.put("higher", "true_false");
				}
				generalStats.put(data);
			}
			else
			{
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getString("deaths_per_game"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getString("deaths_per_game"));
				if (players.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
					data.put("higher", "false_true");
				}
				else
				{
					data.put("higher", "false_false");
				}
				generalStats.put(data);
			}
			if (Double.parseDouble(players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getString("assists_per_game").replace(",", "."))>Double.parseDouble(players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getString("assists_per_game").replace(",", "."))) {
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getString("assists_per_game"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getString("assists_per_game"));
				if (players.getJSONObject(i).getString("team").contains(ownerTeam)) {
					data.put("higher", "true_true");
				}
				else
				{
					data.put("higher", "true_false");
				}
				generalStats.put(data);
			}
			else
			{
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getString("assists_per_game"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getString("assists_per_game"));
				if (players.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
					data.put("higher", "false_true");
				}
				else
				{
					data.put("higher", "false_false");
				}
				generalStats.put(data);
			}
			if (players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getDouble("winrate")>players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getDouble("winrate")) {
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getDouble("winrate"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getDouble("winrate"));
				if (players.getJSONObject(i).getString("team").contains(ownerTeam)) {
					data.put("higher", "true_true");
				}
				else
				{
					data.put("higher", "true_false");
				}
				generalStats.put(data);
			}
			else
			{
				JSONObject data=new JSONObject();
				data.put("user_1_stat", players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(j).getDouble("winrate"));
				data.put("user_2_stat", players.getJSONObject(i+5).getJSONArray("general_stats").getJSONObject(j).getDouble("winrate"));
				if (players.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
					data.put("higher", "false_true");
				}
				else
				{
					data.put("higher", "false_false");
				}
				generalStats.put(data);
			}
		}
			users.put("general_stats",generalStats);
			users.put("history","None");
			rolesArray.put(users);
		}
		
		return rolesArray;
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
		user.put("team", players.getJSONObject(i).getString("team"));
		user.put("owner", players.getJSONObject(i).getBoolean("owner"));
		user.put("champion", players.getJSONObject(i).getString("champion_key"));
		user.put("champion_name", players.getJSONObject(i).getString("champion_name"));
		JSONArray prefferChamps=new JSONArray();
		try
		{
		prefferChamps.put(players.getJSONObject(i).getJSONArray("general_stats").getJSONArray(2).getJSONObject(0).getString("champion_key"));
		prefferChamps.put(players.getJSONObject(i).getJSONArray("general_stats").getJSONArray(2).getJSONObject(1).getString("champion_key"));
		prefferChamps.put(players.getJSONObject(i).getJSONArray("general_stats").getJSONArray(2).getJSONObject(2).getString("champion_key"));
		}
		catch(Exception e)
		{
			
		}
		user.put("preffered_champions", prefferChamps);
		user.put("history", "None");
		if (players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(0).getString("name").contains("all") || players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(0).getString("name").contains("unranked")) {
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
		int counterKDA = 10;
		int counterGames = 10;
		Double winrate = 0d;
		Double kills = 0d;
		Double deaths = 0d;
		Double assists = 0d;
		Double kda = 0d;
		int games = 0;
		try {
			games = players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info).getInt("games_played");

			for (int k = 0; k < players.length(); k++) {

				if (games >=players.getJSONObject(k).getJSONArray("general_stats").getJSONObject(info).getInt("games_played")) {
					counterGames--;

				}
			}

		} catch (Exception e) {
			
		}
		try {
			kda =players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info).getDouble("kda");

			for (int k = 0; k < players.length(); k++) {

				if (kda >= players.getJSONObject(k).getJSONArray("general_stats").getJSONObject(info).getDouble("kda")) {
					counterKDA--;

				}
			}

		} catch (Exception e) {
			System.out.println(e);
		}
		try {
			kills = Double.parseDouble(players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info)
					.getString("kills_per_game").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (kills >= Double.parseDouble(players.getJSONObject(k).getJSONArray("general_stats").getJSONObject(info).getString("kills_per_game").replace(",", "."))) {
					counter--;

				}
			}

		} catch (Exception e) {
			
		}
		try {
			assists = Double.parseDouble(players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info)
					.getString("assists_per_game").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (assists >= Double.parseDouble(players.getJSONObject(k).getJSONArray("general_stats").getJSONObject(info).getString("assists_per_game").replace(",", "."))) {
					counter_assists--;

				}

			}
		} catch (Exception e) {
			
		}
		try {
			deaths = Double.parseDouble(players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info)
					.getString("deaths_per_game").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (deaths >= Double.parseDouble(players.getJSONObject(k).getJSONArray("general_stats")
						.getJSONObject(info).getString("deaths_per_game").replace(",", "."))) {
					counter_deaths--;

				}

			}
		} catch (Exception e) {
			
		}
		try {

			winrate = players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info).getDouble("winrate");

			for (int k = 0; k < players.length(); k++) {

				if (winrate >= players.getJSONObject(i).getJSONArray("general_stats").getJSONObject(info)
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
		JSONObject kdaInfo = new JSONObject();
		JSONObject gamesInfo = new JSONObject();
		
		gamesInfo.put("stat", games);
		gamesInfo.put("counter", counterGames);
		gamesInfo.put("label", "Games Played");
		kdaInfo.put("stat", kda);
		kdaInfo.put("counter", counterKDA);
		kdaInfo.put("label", "KDA");
		killsInfo.put("stat", kills);
		killsInfo.put("counter", counter);
		killsInfo.put("label", "Kills");
		assistsInfo.put("stat", assists);
		assistsInfo.put("counter", counter_assists);
		assistsInfo.put("label", "Assists");
		deathsInfo.put("stat", deaths);
		deathsInfo.put("counter", counter_deaths);
		deathsInfo.put("label", "Deaths");
		winrateInfo.put("counter", counter_winrate);
		winrateInfo.put("stat", winrate);
		winrateInfo.put("label", "winrate");
		userInfo.put(gamesInfo);
		userInfo.put(kdaInfo);
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
				champion.put("kda", 0);
				NumberFormat formatter = new DecimalFormat("#0.0");
				NumberFormat formatter_winrate = new DecimalFormat("#0.00");

				champion.put("kills_per_game", Integer.toString(0));
				champion.put("deaths_per_game", Integer.toString(0));
				champion.put("assists_per_game", Integer.toString(0));

				champion.put("winrate", Integer.toString(0));

				champion.put("loserate", Integer.toString(0));
				championList.put(champion);
			}
			
			championList.put(getSortedList(allChampionList));
		} catch (Exception e) {
			JSONObject champion1 = new JSONObject();
			que = summoner.getStats(Season.SEASON2016);
			for (Entry<PlayerStatsSummaryType, PlayerStatsSummary> entry : que.entrySet()) {

				JSONObject champion = new JSONObject();
				if (entry.getKey().toString().contentEquals("Unranked")) {
					champion.put("name", "unranked");
					champion.put("games_played", 0);
					champion.put("kills", round(0, 1));
					champion.put("assists", round(0, 1));
					champion.put("deaths", round(0, 1));
					champion.put("loses", 0);
					champion.put("wins", 0);

					NumberFormat formatter = new DecimalFormat("#0.0");
					NumberFormat formatter_winrate = new DecimalFormat("#0.00");
					Double killsPerGame;
					Double deathsPerGame;
					Double assistsPerGame;
					if (entry.getValue().getAggregatedStats().getTotalGamesPlayed()==0) {
						 killsPerGame = 0d;
						 deathsPerGame = 0d;
						 assistsPerGame = 0d;
						 
					}
					else
					{
						 killsPerGame = 0d;
						 deathsPerGame = 0d;
						 assistsPerGame = 0d;
					}
				
					
					Double winrate = (double) (entry.getValue().getAggregatedStats().getTotalWins())
							/ (double) (entry.getValue().getAggregatedStats().getTotalGamesPlayed()) * 100;
					if (deathsPerGame==0) {
						champion.put("kda", round((killsPerGame + assistsPerGame), 2));
					}
					else
					{
					champion.put("kda", round((killsPerGame + assistsPerGame) / deathsPerGame, 2));
					}
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
			champion1.put("kda", 0);

			champion1.put("kills_per_game", Integer.toString(0));
			champion1.put("deaths_per_game", Integer.toString(0));
			champion1.put("assists_per_game", Integer.toString(0));

			champion1.put("winrate", Integer.toString(0));

			champion1.put("loserate", Integer.toString(0));
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
		if (string.equals("all")) {
			champion.put("champion_key","all");
		}
		else{
		Champion ch = RiotAPI.getChampionByName(string);
		champion.put("champion_key",ch.getKey());
	}
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
		if (deathsPerGame==0) {
			champion.put("kda", round((killsPerGame + assistsPerGame), 2));
		}
		else
		{
		champion.put("kda", round((killsPerGame + assistsPerGame) / deathsPerGame, 2));
		}

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
		for (int i = 0; i < 10; i++) {
			System.out.println(listmatch.get(i).getID());
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
					Champion ch = RiotAPI.getChampionByName(listmatch.get(i).getChampion().toString());
					stats.put("champion_key", ch.getKey());
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
		getHistory(summoner);
		List<Game> games = RiotAPI.getRecentGames(summoner);
		List<MatchReference> listmatch = summoner.getMatchList();
		JSONArray history = new JSONArray();
		int kills=0;
		int assists=0;
		int deaths=0;
		int wins=0;
		
		
		for (int i = 0; i < 10; i++) {
			int counter = 0;
			try {

				JSONObject stats = new JSONObject();
				stats.put("win", games.get(i).getStats().getWin());

				stats.put("matchid", String.valueOf(listmatch.get(i).getID()));
				stats.put("champion", games.get(i).getChampion());
				Champion ch = RiotAPI.getChampionByName(games.get(i).getChampion().toString());
				stats.put("champion_key", ch.getKey());
				stats.put("assists", games.get(i).getStats().getAssists());
				stats.put("deaths", games.get(i).getStats().getDeaths());
				stats.put("kills", games.get(i).getStats().getKills());
				stats.put("kda", round((double)(games.get(i).getStats().getKills() + games.get(i).getStats().getAssists()) /  (double)games.get(i).getStats().getDeaths(), 2));
				
				stats.put("item_0", games.get(i).getStats().getItem0ID());
				stats.put("item_1", games.get(i).getStats().getItem1ID());
				stats.put("item_2", games.get(i).getStats().getItem2ID());
				stats.put("item_3", games.get(i).getStats().getItem3ID());
				stats.put("item_4", games.get(i).getStats().getItem4ID());
				stats.put("item_5", games.get(i).getStats().getItem5ID());
				
				history.put(stats);
				
				kills+=games.get(i).getStats().getKills();
				assists+=games.get(i).getStats().getAssists();
				deaths+=games.get(i).getStats().getDeaths();
				if (games.get(i).getStats().getWin()) {
					wins++;
				}

			} catch (Exception e) {
				System.out.println(e);
			}
		}
		Double kda=0d;
		if (deaths==0) {
			kda=round((kills + assists), 2);
		}
		else
		{
			kda=round((kills + assists)/deaths, 2);
		}
		Double killsPer=(double) kills/10;
		Double daethsPer=(double) deaths/10;
		Double assistsPer=(double) assists/10;
		Double winrate=(double) wins/10;
		winrate=winrate*100;
		JSONObject historyInfo=new JSONObject();
		historyInfo.put("kda", round(kda,2));
		historyInfo.put("kills_per_game", killsPer);
		historyInfo.put("kda_rate", "none");
		historyInfo.put("kills_rate", "none");
		historyInfo.put("death_rate", "none");
		historyInfo.put("assists_rate", "none");
		historyInfo.put("winrate_rate", "none");
		historyInfo.put("assists_per_game", daethsPer);
		historyInfo.put("deaths_per_game", assistsPer);
		historyInfo.put("winrate", winrate);
		
	
		history.put(historyInfo);
		
		return history;
	}
	private JSONArray getHistoryInfo(JSONObject players) {
		int counter = 10;
		int counter_assists = 10;
		int counter_deaths = 10;
		int counter_winrate = 10;
		int counterKDA = 10;
		Double winrate = 0d;
		Double kills = 0d;
		Double deaths = 0d;
		Double assists = 0d;
		Double kda = 0d;
		try {
			kda = Double.parseDouble(players.getString("kda").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (kda > Double.parseDouble(players.getString("kda").replace(",", "."))) {
					counterKDA--;

				}
			}

		} catch (Exception e) {
			
		}
		try {
			kills = Double.parseDouble(players.getString("kills_per_game").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (kills > Double.parseDouble(players.getString("kills_per_game").replace(",", "."))) {
					counter--;

				}
			}

		} catch (Exception e) {
			
		}
		try {
			assists = Double.parseDouble(players.getString("assists_per_game").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (assists > Double.parseDouble(players.getString("assists_per_game").replace(",", "."))) {
					counter_assists--;

				}

			}
		} catch (Exception e) {
			
		}
		try {
			deaths = Double.parseDouble(players.getString("deaths_per_game").replace(",", "."));

			for (int k = 0; k < players.length(); k++) {

				if (deaths > Double.parseDouble(players.getString("deaths_per_game").replace(",", "."))) {
					counter_deaths--;

				}

			}
		} catch (Exception e) {
			
		}
		try {

			winrate = players.getDouble("winrate");

			for (int k = 0; k < players.length(); k++) {

				if (winrate > players.getDouble("winrate")) {
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
		JSONObject kdaInfo = new JSONObject();
		
		kdaInfo.put("stat", kda);
		kdaInfo.put("counter", counterKDA);
		kdaInfo.put("label", "KDA");
		killsInfo.put("stat", kills);
		killsInfo.put("counter", counter);
		killsInfo.put("label", "Kills");
		assistsInfo.put("stat", assists);
		assistsInfo.put("counter", counter_assists);
		assistsInfo.put("label", "Assists");
		deathsInfo.put("stat", deaths);
		deathsInfo.put("counter", counter_deaths);
		deathsInfo.put("label", "Assists");
		winrateInfo.put("counter", counter_winrate);
		winrateInfo.put("stat", winrate);
		winrateInfo.put("label", "winrate");
		userInfo.put(kdaInfo);
		userInfo.put(killsInfo);
		userInfo.put(assistsInfo);
		userInfo.put(deathsInfo);
		userInfo.put(winrateInfo);
		return userInfo;
	}

	@Override
	public JSONArray getHistoryRoles(JSONObject data_get) {
		JSONArray data=data_get.getJSONArray("get_data");
		JSONArray rolesArray=new JSONArray();
		String ownerTeam="";
		for (int i = 0; i < 10; i++) {
			if (data.getJSONObject(i).getBoolean("owner")) {
				ownerTeam=data.getJSONObject(i).getString("team");
			}
		}
			for (int i = 0; i < 1; i++) {
				JSONObject users=new JSONObject();
				
				
				JSONArray generalStats=new JSONArray();
				
				
				
				if (data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("kda")>data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("kda")) {
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("kda"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("kda"));
					if (data.getJSONObject(i).getString("team").contains(ownerTeam)) {
						data1.put("higher", "true_true");
					}
					else
					{
						data1.put("higher", "true_false");
					}
					generalStats.put(data1);
				}
				else
				{
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("kda"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("kda"));
					if (data.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
						data1.put("higher", "false_true");
					}
					else
					{
						data1.put("higher", "false_false");
					}
					generalStats.put(data1);
				}
				if (data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("kills_per_game")>data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("kills_per_game")) {
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("kills_per_game"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("kills_per_game"));
					if (data.getJSONObject(i).getString("team").contains(ownerTeam)) {
						data1.put("higher", "true_true");
					}
					else
					{
						data1.put("higher", "true_false");
					}
					generalStats.put(data1);
				}
				else
				{
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("kills_per_game"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("kills_per_game"));
					if (data.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
						data1.put("higher", "false_true");
					}
					else
					{
						data1.put("higher", "false_false");
					}
					generalStats.put(data1);
				}
				if (data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("deaths_per_game")>data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("deaths_per_game")) {
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("deaths_per_game"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("deaths_per_game"));
					if (data.getJSONObject(i).getString("team").contains(ownerTeam)) {
						data1.put("higher", "true_true");
					}
					else
					{
						data1.put("higher", "true_false");
					}
					generalStats.put(data1);
				}
				else
				{
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("deaths_per_game"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("deaths_per_game"));
					if (data.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
						data1.put("higher", "false_true");
					}
					else
					{
						data1.put("higher", "false_false");
					}
					generalStats.put(data1);
				}
				if (data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("assists_per_game")>data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("assists_per_game")) {
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("assists_per_game"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("assists_per_game"));
					if (data.getJSONObject(i).getString("team").contains(ownerTeam)) {
						data1.put("higher", "true_true");
					}
					else
					{
						data1.put("higher", "true_false");
					}
					generalStats.put(data1);
				}
				else
				{
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("assists_per_game"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("assists_per_game"));
					if (data.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
						data1.put("higher", "false_true");
					}
					else
					{
						data1.put("higher", "false_false");
					}
					generalStats.put(data1);
				}
				if (data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("winrate")>data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("winrate")) {
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("winrate"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("winrate"));
					if (data.getJSONObject(i).getString("team").contains(ownerTeam)) {
						data1.put("higher", "true_true");
					}
					else
					{
						data1.put("higher", "true_false");
					}
					generalStats.put(data1);
				}
				else
				{
					JSONObject data1=new JSONObject();
					data1.put("user_1_stat", data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("winrate"));
					data1.put("user_2_stat", data.getJSONObject(i+1).getJSONArray("history").getJSONObject(10).getDouble("winrate"));
					if (data.getJSONObject(i+5).getString("team").contains(ownerTeam)) {
						data1.put("higher", "false_true");
					}
					else
					{
						data1.put("higher", "false_false");
					}
					generalStats.put(data1);
				}
			
				users.put("history_stats",generalStats);
				
				rolesArray.put(users);
			}
			
			rolesArray.put(getHistoyStats(data));
		return rolesArray;
	}

	private JSONArray getHistoyStats(JSONArray data) {
		JSONArray finalData = new JSONArray();
		for (int i = 0; i < 10; i++) {
			int counter = 10;
			int counter_assists = 10;
			int counter_deaths = 10;
			int counter_winrate = 10;
			int counterKDA = 10;
			Double winrate = 0d;
			Double kills = 0d;
			Double deaths = 0d;
			Double assists = 0d;
			Double kda = 0d;
		
		try {
			kda = data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("kda");

			for (int k = 0; k < 10; k++) {

				if (kda > data.getJSONObject(k).getJSONArray("history").getJSONObject(10).getDouble("kda")) {
					counterKDA--;

				}
			}

		} catch (Exception e) {
			
		}
		try {
			kills =data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("kills_per_game");

			for (int k = 0; k < 10; k++) {

				if (kills > data.getJSONObject(k).getJSONArray("history").getJSONObject(10).getDouble("kills_per_game")) {
					counter--;

				}
			}

		} catch (Exception e) {
			
		}
		try {
			assists =data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("assists_per_game");

			for (int k = 0; k < 10; k++) {

				if (assists > data.getJSONObject(k).getJSONArray("history").getJSONObject(10).getDouble("assists_per_game")) {
					counter_assists--;

				}
			}

		} catch (Exception e) {
			
		}
		try {
			deaths =data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("deaths_per_game");

			for (int k = 0; k < 10; k++) {

				if (kills > data.getJSONObject(k).getJSONArray("history").getJSONObject(10).getDouble("deaths_per_game")) {
					counter_deaths--;

				}
			}

		} catch (Exception e) {
			
		}
		try {
			winrate =data.getJSONObject(i).getJSONArray("history").getJSONObject(10).getDouble("winrate");

			for (int k = 0; k < 10; k++) {

				if (kills > data.getJSONObject(k).getJSONArray("history").getJSONObject(10).getDouble("winrate")) {
					counter_winrate--;

				}
			}

		} catch (Exception e) {
			
		}
		JSONArray userInfo = new JSONArray();
		JSONObject genData = new JSONObject();
		JSONObject killsInfo = new JSONObject();
		JSONObject assistsInfo = new JSONObject();
		JSONObject deathsInfo = new JSONObject();
		JSONObject winrateInfo = new JSONObject();
		JSONObject kdaInfo = new JSONObject();
		
		kdaInfo.put("stat", kda);
		kdaInfo.put("counter", counterKDA);
		kdaInfo.put("label", "KDA");
		killsInfo.put("stat", kills);
		killsInfo.put("counter", counter);
		killsInfo.put("label", "Kills");
		assistsInfo.put("stat", assists);
		assistsInfo.put("counter", counter_assists);
		assistsInfo.put("label", "Assists");
		deathsInfo.put("stat", deaths);
		deathsInfo.put("counter", counter_deaths);
		deathsInfo.put("label", "Assists");
		winrateInfo.put("counter", counter_winrate);
		winrateInfo.put("stat", winrate);
		winrateInfo.put("label", "winrate");
		userInfo.put(kdaInfo);
		userInfo.put(killsInfo);
		userInfo.put(assistsInfo);
		userInfo.put(deathsInfo);
		userInfo.put(winrateInfo);
		genData.put("history_rates", userInfo);
		finalData.put(genData);
		}
		
		return finalData;
	}
	private JSONArray newList(JSONArray players) {
		// TODO Auto-generated method stub
		List<JSONObject> returnArray=  Arrays.asList(new JSONObject[10]);
		List<Integer> list = new ArrayList<Integer>();
	
		int exhPosTeam1=2;
		int healPosTeam1=1;
		int exhPosTeam2=7;
		int healPosTeam2=6;
	
		for (int i = 0; i < 10; i++) {
			list.add(i);
		}
		for (int i = 0; i < 10; i++) {
			if (players.getJSONObject(i).getString("rune1").contains("Smite") || players.getJSONObject(i).getString("rune2").contains("Smite")) {
				list.set(i, 10);
				if (i<5) {
					
					returnArray.set(0, players.getJSONObject(i));
				}
				else
				{
				
					returnArray.set(5, players.getJSONObject(i));
				}

			}
			if (players.getJSONObject(i).getString("rune1").contains("Heal") || players.getJSONObject(i).getString("rune2").contains("Heal")) {
				list.set(i, 10);
				if (i<5) {
					
					returnArray.set(healPosTeam1, players.getJSONObject(i));
					healPosTeam1=3;
				}
				else
				{
					
					returnArray.set(healPosTeam2, players.getJSONObject(i));
					healPosTeam2=8;
				}
				
			}
			if (players.getJSONObject(i).getString("rune1").contains("Exhaust") || players.getJSONObject(i).getString("rune2").contains("Exhaust")) {
				list.set(i, 10);
				if (i<5) {
					
					returnArray.set(exhPosTeam1, players.getJSONObject(i));
					exhPosTeam1=4;
				}
				else
				{
					
					returnArray.set(exhPosTeam2, players.getJSONObject(i));
					exhPosTeam2=9;
				}
				
			}
			
			
		}
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i)!=10) {
				if (i<5) {
					
					list.set(i, 10);
					
					for (int j = 0; j < 5; j++) {
						if (returnArray.get(j)==null) {
							returnArray.set(j, players.getJSONObject(i));
						}
						
					}
					
				}
				else
				{
					list.set(i, 10);
					for (int j = 5; j < 10; j++) {
						if (returnArray.get(j)==null) {
							returnArray.set(j, players.getJSONObject(i));
						}
						
					}
				}
			}
			
		}
		return new JSONArray(returnArray);
	}

	@Override
	public JSONArray getSort(JSONArray data_get) {
		// TODO Auto-generated method stub
		List<JSONObject> returnArray=  Arrays.asList(new JSONObject[10]);
		JSONArray returnList=statsLive.getJSONObject(0).getJSONArray("players");
		for (int i = 0; i <10; i++) {
			returnArray.set(data_get.getInt(i), returnList.getJSONObject(i));
			
		}
		statsLive.getJSONObject(0).put("players", new JSONArray(returnArray));
		JSONArray roles = getRolesStats(new JSONArray(returnArray));

		statsLive.getJSONObject(0).put("roles", roles);
		return statsLive;
	}
}
