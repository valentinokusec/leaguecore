package com.leaguelove.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="match")
public class Match {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;
	@Column(name="match_id")
	private Long match_id;
	@Column(name="length")
	private Long length;
	@Column(name="que")
	private String que;
	

	public Match(Long match_id, Long length, String que)
	{
		
		this.match_id=match_id;
		this.length=length;
		this.que=que;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMatch_id() {
		return match_id;
	}
	public void setMatch_id(Long match_id) {
		this.match_id = match_id;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}
	public String getQue() {
		return que;
	}
	public void setQue(String que) {
		this.que = que;
	}
	
		
}
