package com.leaguelove.services;

import org.springframework.stereotype.Service;

import com.leaguelove.domains.Summoners;



@Service
public interface SummonersService {
	public Summoners findByName(String name);

} 
