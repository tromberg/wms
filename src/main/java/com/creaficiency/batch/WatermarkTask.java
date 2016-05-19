package com.creaficiency.batch;

import javax.batch.api.BatchProperty;
import javax.batch.api.Batchlet;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.creaficiency.boundary.WatermarkService;

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
	
	/**
	 * The wmsDocId property must be set in the job definition
	 */
	@Inject @BatchProperty 
	String wmsDocId;
	
	@Inject 
	WatermarkService wms;
	
	@Override
	public String process() throws Exception {
		Thread.sleep(600); // representing some long-running operation
		wms.addWatermark(Long.parseLong(wmsDocId));
		return "COMPLETED";
	}

	@Override
	public void stop() throws Exception {
		// no action needed
		
	}

	

}
