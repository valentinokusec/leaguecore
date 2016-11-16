package com.leaguelove.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.leaguelove.domain.Match;


public interface MatchDao extends  JpaRepository< Match ,Integer>{

}
