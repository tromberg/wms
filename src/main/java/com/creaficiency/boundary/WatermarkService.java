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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.creaficiency.entity.WatermarkDoc;

/**
 * The Watermark Service
 * @author timr
 *
 */
@Path("/wms")
public class WatermarkService {
	private static Logger LOGGER = Logger.getLogger(WatermarkService.class.getName());	

	@PersistenceUnit
	EntityManagerFactory emf;
	
	EntityManager em;

	@Inject
	UserTransaction utx;

	/**
	 * 
	 * @param Document to watermark
	 * @return ticket id
	 * @throws Exception
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String submitDocForWatermark(WatermarkDoc wmd) throws Exception {
		String theId;
		
		if (wmd == null) throw new NullPointerException();
		wmd.validate();
		
		em = emf.createEntityManager();
		utx.begin();
		em.joinTransaction();
		
		try {
			em.persist(wmd);
			theId = wmd.getId().toString();
			utx.commit();
		}
		catch (Exception ex) {
			utx.rollback();
			em.clear();
			throw ex;
		}
		
		LOGGER.info("Doc " + theId + " submitted.");
		JobOperator jobOperator = BatchRuntime.getJobOperator();
		Properties props = new Properties();
		props.setProperty(PROP_DOCID, theId);
		jobOperator.start(JOB_WMS, props);
		
		em.clear();
		
		return theId;
	}
	
	/**
	 * 
	 * @param id
	 * @return detached watermark doc, or null if it does not exist
	 * @throws Exception
	 */
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public WatermarkDoc getWatermarkDocById(@PathParam("id") String id) throws Exception {
		long numId = Long.parseLong(id);
		em = emf.createEntityManager();
		
		WatermarkDoc result = (WatermarkDoc) em.find(WatermarkDoc.class, numId);
		
		em.clear();
		return result;
	}
	
	public void addWatermark(long id) throws Exception {
		em = emf.createEntityManager();
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
