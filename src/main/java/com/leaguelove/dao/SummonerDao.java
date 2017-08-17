package com.leaguelove.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leaguelove.domains.Summoners;

public interface SummonerDao extends  JpaRepository< Summoners ,Integer>{
	public Summoners findAllByName(String name);

}
