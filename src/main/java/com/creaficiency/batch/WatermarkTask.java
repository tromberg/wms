package com.creaficiency.batch;

import java.util.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.api.Batchlet;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import com.creaficiency.entity.WatermarkDoc;

/**
 * Batchlet for adding the watermark
 * @author timr
 *
 */
// Batchlet needs to be referred by name in order to receive injections
// https://developer.jboss.org/thread/249833?start=0&tstart=0
@Named("WmTask")
@Dependent
public class WatermarkTask implements Batchlet {
	private static Logger LOGGER = Logger.getLogger(WatermarkTask.class.getName());
	/**
	 * The wmsDocId property must be set in the job definition
	 */
	@Inject @BatchProperty 
	String wmsDocId;
	
	@PersistenceContext
	EntityManager em;

	@Inject
	UserTransaction utx;
	
	@Override
	public String process() throws Exception {
		Thread.sleep(600); // representing some long-running operation
		addWatermark(Long.parseLong(wmsDocId));
		return "COMPLETED";
	}

	@Override
	public void stop() throws Exception {
		// no action needed
		
	}
	
	private void addWatermark(long id) throws Exception {
		//em = emf.createEntityManager();
		utx.begin();
		em.joinTransaction();
		try {
			WatermarkDoc doc = em.find(WatermarkDoc.class, id);
			if (doc == null) {
				LOGGER.severe("Doc " + id + " not found for watermarking");
				utx.rollback();
				em.clear();
				return;
			}
			doc.setWatermark(Integer.toString(doc.calcWatermarkCode()));
			
			LOGGER.info("Doc " + id +" watermark set to " + doc.getWatermark());
			
			utx.commit();
			em.clear();
		}
		catch (Exception ex) {
			utx.rollback();
			em.clear();
			throw ex;
		}
	}

	

}
