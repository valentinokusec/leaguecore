package com.leaguelove.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leaguelove.dao.SummonerDao;
import com.leaguelove.domains.Summoners;

@Service
public class SummonersServiceImpl implements SummonersService {

	@Autowired
	private SummonerDao summonersdao;

	@Override
	public Summoners findByName(String name) {
		// TODO Auto-generated method stub
		return summonersdao.findAllByName(name);
	}

	
}
