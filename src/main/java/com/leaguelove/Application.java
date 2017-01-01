package com.leaguelove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.robrua.orianna.api.core.RiotAPI;
import com.robrua.orianna.store.HibernateDB;
import com.robrua.orianna.type.core.common.Region;

@SpringBootApplication
@EnableJpaRepositories("com.leaguelove.dao")
@EnableAutoConfiguration
public class Application {
	private static final String API_KEY="172d9054-b070-449d-bb68-cbbe94f29e7c";
    public static void main(String[] args) {
    	
    	
    	RiotAPI.setRegion(Region.EUW);
        RiotAPI.setAPIKey(API_KEY);
    
        HibernateDB db = HibernateDB.builder().URL("jdbc:mysql://localhost:3306/leaguecore").username("root").password("ireliaftw").build();
        RiotAPI.setDataStore(db);
        SpringApplication.run(Application.class, args);
    }

}
