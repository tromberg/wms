package com.creaficiency.boundary;

import static com.creaficiency.batch.BatchConstants.JOB_WMS;
import static com.creaficiency.batch.BatchConstants.PROP_DOCID;

import java.util.Properties;
import java.util.logging.Logger;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

import com.creaficiency.entity.WatermarkDoc;

/**
 * The Watermark Service
 * @author timr
 *
 */
public class WatermarkService {
	private static Logger LOGGER = Logger.getLogger(WatermarkService.class.getName());	

	@PersistenceUnit
	EntityManagerFactory emf;
	
	EntityManager em;

	@Inject
	UserTransaction utx;

	public long submitDocForWatermark(WatermarkDoc wmd) throws Exception {
		em = emf.createEntityManager();
		utx.begin();
		em.joinTransaction();
		if (wmd == null) throw new NullPointerException();
		wmd.validate();
		
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
		em = emf.createEntityManager();
		WatermarkDoc result = (WatermarkDoc) em.find(WatermarkDoc.class, id);
		em.clear();
		return result;
	}
	
	public void addWatermark(long id) throws Exception {
		em = emf.createEntityManager();
		LOGGER.info("addWatermark SVC " + this.toString() + "EM " + em.toString() + " Thread " + Thread.currentThread().getName());
		utx.begin();
		em.joinTransaction();
		WatermarkDoc doc = em.find(WatermarkDoc.class, id);
		doc.setWatermark(Integer.toString(doc.calcWatermarkCode()));
		LOGGER.info("WATERMARK SET to " + doc.getWatermark());
		utx.commit();
		em.clear();
	}
}
