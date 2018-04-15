/**
 * Copyright (C) 2018 Peter Nagy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * ======================================================================
 *
 * @author Peter Nagy - https://peternagy.ie
 * @since March 2018
 * @version 0.1
 * @description SitesFileExtractorTest - SitesFileExtractor test cases
 * @package ie.peternagy.alexa.top.api.service
 */
package ie.peternagy.alexa.top.api.service;

import ie.peternagy.alexa.top.api.cli.Environment;
import ie.peternagy.alexa.top.api.resources.sites.model.Site;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class SitesFileExtractorTest {
    private static TopSitesUpdater updater = new TopSitesUpdater();
    private static String filePath = Environment.getOrDefault("TOP_SITES_PATH", "/tmp/top-1m.csv.zip");
    
    public SitesFileExtractorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        if(!Files.exists(Paths.get(filePath))){
            if(!updater.downloadExternalData(updater.TMP_SITES_FILE_PATH)){
                throw new RuntimeException("Failed to get external sites data");
            }
            
            filePath = updater.TMP_SITES_FILE_PATH;
        }
    }
    
    @AfterClass
    public static void tearDownClass() throws IOException {
        if(Files.exists(Paths.get(updater.TMP_SITES_FILE_PATH)))
            Files.delete(Paths.get(updater.TMP_SITES_FILE_PATH));
    }
    
    @Before
    public void setUp() {
        System.out.println("Running tests on: " + SitesFileExtractor.class.getName());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of parseFile method, of class SitesFileExtractor.
     */
    @Test
    public void testParseFile() throws InterruptedException {
        System.out.println("parseFile");
        BlockingQueue<Site> sites = new ArrayBlockingQueue<>(20000);
        SitesFileExtractor instance = new SitesFileExtractor(filePath, sites);
        new Thread(() -> {
            instance.parseFile();
        }).start();
        
        Thread.sleep(1000);
        
        assertTrue(sites.size() > 0);
        assertFalse(instance.isStopProcessing());
    }

    /**
     * Test of isIsComplete method, of class SitesFileExtractor.
     */
    @Test
    public void testIsIsComplete() throws InterruptedException {
        System.out.println("isIsComplete");
        
        BlockingQueue<Site> sites = new ArrayBlockingQueue<>(20000);
        SitesFileExtractor instance = new SitesFileExtractor(filePath, sites);
        
        new Thread(() -> {
            instance.parseFile();
        }).start();
        
        Thread.sleep(8);
        
        boolean expResult = false;
        boolean result = instance.isIsComplete();
        assertEquals(expResult, result);
        assertTrue(sites.size() > 0);
        
        instance.setStopProcessing(true);
    }


    /**
     * Test of isStopProcessing method, of class SitesFileExtractor.
     */
    @Test
    public void testIsStopProcessing() throws InterruptedException {
        System.out.println("isStopProcessing");
        BlockingQueue<Site> sites = new ArrayBlockingQueue<>(20000);
        SitesFileExtractor instance = new SitesFileExtractor(filePath, sites);
        new Thread(() -> {
            instance.parseFile();
        }).start();
        
        Thread.sleep(100);
        
        boolean expResult = false;
        boolean result = instance.isStopProcessing();
        assertEquals(expResult, result);
        assertTrue(sites.size() > 0);
        
        instance.setStopProcessing(true);
    }

    /**
     * Test of setStopProcessing method, of class SitesFileExtractor.
     */
    @Test
    public void testSetStopProcessing() throws InterruptedException {
        System.out.println("setStopProcessing");
        
        boolean stopProcessing = true;
        BlockingQueue<Site> sites = new ArrayBlockingQueue<>(20000);
        SitesFileExtractor instance = new SitesFileExtractor(filePath, sites);
        new Thread(() -> {
            instance.parseFile();
        }).start();
        
        Thread.sleep(100);
        
        instance.setStopProcessing(stopProcessing);
        int size = sites.size();
        
        
        Thread.sleep(100);
        
        assertTrue(size == sites.size());
    }

    /**
     * Test of run method, of class SitesFileExtractor.
     */
    @Test
    public void testRun() throws InterruptedException {
        System.out.println("run");
        boolean stopProcessing = true;
        BlockingQueue<Site> sites = new ArrayBlockingQueue<>(20000);
        SitesFileExtractor instance = new SitesFileExtractor(filePath, sites);
        new Thread(instance).start();
        int size = sites.size();
        
        Thread.sleep(100);
        assertTrue(size < sites.size());
        
        
        instance.setStopProcessing(stopProcessing);
        size = sites.size();
        Thread.sleep(100);
        
        assertTrue(size == sites.size());
    }
    
}
