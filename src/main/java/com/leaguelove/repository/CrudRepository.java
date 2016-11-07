package com.leaguelove.repository;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;

import com.leaguelove.domain.LeadBoardModel;



public class CrudRepository extends CouchDbRepositorySupport<LeadBoardModel> {
	
	public CrudRepository(CouchDbConnector dbc) {
		super(LeadBoardModel.class, dbc, true);
	}
	
}
