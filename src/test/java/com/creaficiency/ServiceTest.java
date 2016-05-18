package com.creaficiency;

import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.creaficiency.boundary.WatermarkService;
import com.creaficiency.entity.WatermarkDoc;

import junit.framework.TestCase;

@RunWith(Arquillian.class)
public class ServiceTest extends TestCase {
	public static final Logger LOGGER = Logger.getLogger(ServiceTest.class.getName());
	
	@Inject WatermarkService wms;

	String title;
	long bookId;
	
	@Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "com.creaficiency")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/batch-jobs/watermark.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }


   
    @Test(expected = IllegalArgumentException.class)
    public void submit_null_not_allowed() throws Exception {
        wms.submitDocForWatermark(null);
        
       
    }

    @Test
    public void valid_book_can_be_submitted() throws Exception {
        title = UUID.randomUUID().toString();
    	bookId = wms.submitDocForWatermark(new WatermarkDoc(title));        
    	Assert.assertTrue(bookId > 0);
    	
    	WatermarkDoc doc = wms.getWatermarkDocById(bookId);
    	Assert.assertNotNull(doc);
    	Assert.assertEquals(title, doc.getTitle());
    	
    	int r = 10;
    	while (--r >= 0 && doc.getWatermark() == null) {
    		Thread.sleep(250);
    		
    		wms.getWatermarkDocById(bookId);
    	}
    	
    	Assert.assertTrue("Timeout waiting for Watermark", r >= 0);
    }
    
    
/*    @Test
    public void submitted_book_can_be_retrieved() throws Exception {
    	WatermarkDoc doc = wms.getWatermarkDocById(bookId);
    	Assert.assertNotNull(doc);
    	Assert.assertEquals(title, doc.getTitle());
    }
  */  
}