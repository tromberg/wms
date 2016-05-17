package com.creaficiency.batch;

import java.util.Properties;
import java.util.logging.Logger;

import javax.batch.api.Batchlet;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;

public class WatermarkTask implements Batchlet {

	private static Logger LOGGER = Logger.getLogger(WatermarkTask.class.getName());
	
	@Inject private JobContext jctx;
	
	@Override
	public String process() throws Exception {
		Properties props = jctx.getProperties();
		LOGGER.info("docId:" + props.getProperty("wms-docid"));
		return "COMPLETED";
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		
	}

	

}
