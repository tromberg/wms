package com.creaficiency.boundary;

import static com.creaficiency.batch.BatchConstants.JOB_WMS;
import static com.creaficiency.batch.BatchConstants.PROP_DOCID;

import java.util.Properties;
import java.util.logging.Logger;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import com.creaficiency.entity.WatermarkDoc;

public class WatermarkService {
	private static Logger LOGGER = Logger.getLogger(WatermarkService.class.getName());
	@PersistenceContext
	EntityManager em;

	@Inject
	UserTransaction utx;

	public long submitDocForWatermark(WatermarkDoc wmd) throws Exception {
		utx.begin();
		em.joinTransaction();
		
		em.persist(wmd);
		Long theId = wmd.getId();
		
		JobOperator jobOperator = BatchRuntime.getJobOperator();
		Properties props = new Properties();
		props.setProperty(PROP_DOCID, theId.toString());
		jobOperator.start(JOB_WMS, props);
		
		utx.commit();
		em.clear();
		
		return theId;
		
	}
	
	public WatermarkDoc getWatermarkDocById(long id) throws Exception {
		return (WatermarkDoc) em.find(WatermarkDoc.class, id);
	}
	
	public void addWatermark(long id) throws Exception {
		utx.begin();
		em.joinTransaction();
		WatermarkDoc doc = em.find(WatermarkDoc.class, id);
		doc.setWatermark(doc.getTitle());
		utx.commit();
		em.clear();
	}
}
