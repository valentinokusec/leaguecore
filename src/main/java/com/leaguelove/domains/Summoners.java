package com.leaguelove.domains;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="summoners")
public class Summoners {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	@Column(name="summonerid")
	private Long summonerid;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSummonerid() {
		return summonerid;
	}
	public void setSummonerid(Long summonerid) {
		this.summonerid = summonerid;
	}
	

}
