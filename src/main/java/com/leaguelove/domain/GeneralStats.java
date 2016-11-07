package com.leaguelove.domain;

public class GeneralStats {
	
	private String name;
	private int games_played;
	private int kills;
	private int assists;
	private int deaths;
	private int loses;
	private int wins;
	private String kills_per_game;
	private String deaths_per_game;
	private String assists_per_game;
	private Double winrate;
	private Double loserate;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getGames_played() {
		return games_played;
	}
	public void setGames_played(int games_played) {
		this.games_played = games_played;
	}
	public int getKills() {
		return kills;
	}
	public void setKills(int kills) {
		this.kills = kills;
	}
	public int getAssists() {
		return assists;
	}
	public void setAssists(int assists) {
		this.assists = assists;
	}
	public int getDeaths() {
		return deaths;
	}
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	public int getLoses() {
		return loses;
	}
	public void setLoses(int loses) {
		this.loses = loses;
	}
	public int getWins() {
		return wins;
	}
	public void setWins(int wins) {
		this.wins = wins;
	}
	public String getKills_per_game() {
		return kills_per_game;
	}
	public void setKills_per_game(String kills_per_game) {
		this.kills_per_game = kills_per_game;
	}
	public String getDeaths_per_game() {
		return deaths_per_game;
	}
	public void setDeaths_per_game(String deaths_per_game) {
		this.deaths_per_game = deaths_per_game;
	}
	public String getAssists_per_game() {
		return assists_per_game;
	}
	public void setAssists_per_game(String assists_per_game) {
		this.assists_per_game = assists_per_game;
	}
	public Double getWinrate() {
		return winrate;
	}
	public void setWinrate(Double winrate) {
		this.winrate = winrate;
	}
	public Double getLoserate() {
		return loserate;
	}
	public void setLoserate(Double loserate) {
		this.loserate = loserate;
	}
	
	

}
