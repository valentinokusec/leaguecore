package com.leaguelove.domains;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name="summoner")
public class Summoners {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	@Column(name="summonerid")
	private Long summonerId;
	@Column(name="profileid")
	private Long profileId;
	@Column(name="name")
	private String name;
	
	
	

}
