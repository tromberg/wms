package com.creaficiency;

import java.net.URL;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//import org.glassfish.jersey.filter.LoggingFilter;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.creaficiency.entity.WatermarkDoc;
import com.creaficiency.entity.WatermarkDoc.DocType;

import junit.framework.TestCase;

@RunWith(Arquillian.class)
public class ResourceTest extends TestCase {
    public static final Logger LOGGER = Logger.getLogger(ResourceTest.class.getName());

    private Client client;

    @ArquillianResource
    URL url;

    @Deployment(testable = false)
    public static Archive<?> createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "com.creaficiency")
                .addAsResource("test-persistence.xml", "META-INF/persistence.xml")
                .addAsResource("META-INF/batch-jobs/watermark.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Before
    public void setUp() throws Exception {
        this.client = ClientBuilder.newClient();
        //this.client.register(new LoggingFilter(LOGGER, true));
    }


    @Test
    public void submit_valid_book() throws Exception {
        WebTarget wms = client.target("http://localhost:8080").path("wms");
        WatermarkDoc doc = new WatermarkDoc("title", "author", DocType.BOOK, "subject");
        
        Response r = wms.request(MediaType.TEXT_PLAIN).post(Entity.json(doc));
        Assert.assertEquals(r.getStatus(), 200);
        LOGGER.info("REST result: " + r.readEntity(String.class));
    }



}