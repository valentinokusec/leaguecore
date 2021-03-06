package com.leaguelove.domains;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="gamedetail")
public class GameDetail {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	@Column(name="game_id")
	private Long gameId;
	@Column(name="que_id")
	private int queId;
	@Column(name="game_mode")
	private String gameMode;
	@Column(name="game_creation")
	private Long gameCreation;
	@Column(name="game_duration")
	private Long gameDuration;

	

}
