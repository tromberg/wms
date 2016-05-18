package com.creaficiency;

import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;

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
import com.creaficiency.entity.WatermarkDoc.DocType;

import junit.framework.TestCase;

@RunWith(Arquillian.class)
public class ServiceTest extends TestCase {
	public static final Logger LOGGER = Logger.getLogger(ServiceTest.class.getName());
	
	@Inject WatermarkService wms;

	
	@Deployment
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "com.creaficiency")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/batch-jobs/watermark.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }


   
    @Test(expected = NullPointerException.class)
    public void submit_null_not_allowed() throws Exception {
        wms.submitDocForWatermark(null);   
    }
    
    @Test(expected = ValidationException.class)
    public void null_author_not_allowed() throws Exception {
        WatermarkDoc doc = new WatermarkDoc("war and peace", null, DocType.BOOK, "History");
    	
    	wms.submitDocForWatermark(doc);   
    }

    @Test(expected = ValidationException.class)
    public void book_must_have_topic() throws Exception {
        WatermarkDoc doc = new WatermarkDoc("war and peace", "tolstoy", DocType.BOOK, null);
    	
    	wms.submitDocForWatermark(doc);   
    }

    @Test(expected = ValidationException.class)
    public void journal_must_not_have_topic() throws Exception {
        WatermarkDoc doc = new WatermarkDoc("A modest proposal", "swift", DocType.JOURNAL, "Nutrition");
    	
    	wms.submitDocForWatermark(doc);   
    }


    @Test
    public void valid_docs_can_be_submitted_and_watermarks_are_unique() throws Exception {
        String title = UUID.randomUUID().toString();
        WatermarkDoc doc1 = new WatermarkDoc(title, "Einstein", DocType.BOOK, "Physics");
        String watermark1 = getWatermarkForDoc(doc1);
        
        WatermarkDoc doc2 = new WatermarkDoc(title, "Planck", DocType.JOURNAL, null);
    	String watermark2 = getWatermarkForDoc(doc2);
    	
    	Assert.assertNotEquals(watermark1, watermark2);
    }
    
    private String getWatermarkForDoc(WatermarkDoc doc) throws Exception {
    	
        long docId = wms.submitDocForWatermark(doc);        
    	Assert.assertTrue(docId > 0);
    	
    	WatermarkDoc readDoc = wms.getWatermarkDocById(docId);
    	Assert.assertNotNull(readDoc);
    	Assert.assertEquals(doc.getTitle(), readDoc.getTitle());
    	
    	int r = 10;
    	while (--r >= 0 && readDoc.getWatermark() == null) {
    		Thread.sleep(250);
    		
    		readDoc = wms.getWatermarkDocById(docId);
    		LOGGER.info(readDoc.toString());
    	}
    	
    	Assert.assertTrue("Timeout waiting for Watermark", r >= 0);
    	
    	return readDoc.getWatermark();
    }
    
     
}