package com.leaguelove.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leaguelove.domains.Summoner;

public interface SummonerDao extends  JpaRepository< Summoner ,Integer>{

}
