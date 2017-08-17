package com.leaguelove.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leaguelove.domains.MatchList;
import com.leaguelove.domains.Summoners;

public interface MatchListDao extends  JpaRepository< MatchList ,Integer>{
	public MatchList findAllByMatchId(int id);

}
