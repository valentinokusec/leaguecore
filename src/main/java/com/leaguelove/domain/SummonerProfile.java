package com.leaguelove.domain;

import java.util.ArrayList;

public class SummonerProfile {
	private String name;
	private String champion_name;
	private String profile_icon;
	private ChampionMainStats champion_stats;
	private GeneralStats general_stats;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChampion_name() {
		return champion_name;
	}
	public void setChampion_name(String champion_name) {
		this.champion_name = champion_name;
	}
	public String getProfile_icon() {
		return profile_icon;
	}
	public void setProfile_icon(String profile_icon) {
		this.profile_icon = profile_icon;
	}
	public ChampionMainStats getChampion_stats() {
		return champion_stats;
	}
	public void setChampion_stats(ChampionMainStats champion_stats) {
		this.champion_stats = champion_stats;
	}
	public GeneralStats getGeneral_stats() {
		return general_stats;
	}
	public void setGeneral_stats(GeneralStats general_stats) {
		this.general_stats = general_stats;
	}

	
	
}
