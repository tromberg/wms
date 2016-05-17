package com.creaficiency.boundary;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import com.creaficiency.entity.WatermarkDoc;

public class WatermarkService {
	@PersistenceContext
	EntityManager em;

	@Inject
	UserTransaction utx;

	public long submitDocForWatermark(WatermarkDoc wmd) throws Exception {
		utx.begin();
		em.joinTransaction();
		em.persist(wmd);
		Long theId = wmd.getId();
		utx.commit();
		em.clear();
		return theId;
		
	}
	
	public WatermarkDoc getWatermarkDocById(long id) throws Exception {
		return(WatermarkDoc) em.find(WatermarkDoc.class, id);
	}
}
