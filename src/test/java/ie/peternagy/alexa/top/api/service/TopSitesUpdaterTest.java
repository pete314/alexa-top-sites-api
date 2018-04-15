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
 * @description TopSitesUpdaterTest - test cases for TopSitesUpdater
 * @package ie.peternagy.alexa.top.api.service
 */
package ie.peternagy.alexa.top.api.service;

import ie.peternagy.alexa.top.api.common.client.JedisFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import redis.clients.jedis.Jedis;

@FixMethodOrder
public class TopSitesUpdaterTest {
    
    private static final Date NOW = Calendar.getInstance().getTime();
    
    public TopSitesUpdaterTest() {
    }
    
    protected static void clearCache(){
        try(Jedis jedis = JedisFactory.getInstance().newClient()){
            TopSitesUpdater instance = new TopSitesUpdater();
            jedis.del(instance.CACHE_KEY_UPDATE);
        }
    }
    
    @BeforeClass
    public static void beforeClass(){
        clearCache();
    }
    
    @AfterClass
    public static void afterClass(){
        clearCache();
    }
    
    @Before
    public void setUp() {
        System.out.println("Running tests on: " + TopSitesUpdater.class.getName());
    }
    
    /**
     * Test of getExternalUpdateTime method, of class TopSitesUpdater.
     */
    @Test
    public void testAAGetExternalUpdateTime() {
        System.out.println("getExternalUpdateTime");
        
        TopSitesUpdater instance = new TopSitesUpdater();
        Date expResult = new Date(1);
        Date result = instance.getExternalUpdateTime();
        
        //If all working the date should be newer than epoc
        assertTrue(expResult.before(result));
    }

    

    /**
     * Test of setInternalUpdateTime method, of class TopSitesUpdater.
     */
    @Test
    public void testABSetInternalUpdateTime() {
        System.out.println("setInternalUpdateTime");
        
        TopSitesUpdater instance = new TopSitesUpdater();
        boolean expResult = true;
        boolean result = instance.setInternalUpdateTime(NOW);
        
        assertEquals(expResult, result);
        
        clearCache();
    }

    /**
     * Test of getInternalUpdateTime method, of class TopSitesUpdater.
     */
    @Test
    public void testACGetInternalUpdateTime() {
        System.out.println("getInternalUpdateTime");
        
        TopSitesUpdater instance = new TopSitesUpdater();
        Date result = instance.getInternalUpdateTime();
        
        assertTrue(NOW.after(result));
    }
    
    /**
     * Test of downloadExternalData method, of class TopSitesUpdater.
     */
    @Test
    public void testADDownloadExternalData() throws IOException {
        System.out.println("downloadExternalData");
        
        TopSitesUpdater instance = new TopSitesUpdater();
        String localFilePath = instance.TMP_SITES_FILE_PATH;
        boolean expResult = true;
        boolean result = instance.downloadExternalData(localFilePath);
        
        assertEquals(expResult, result);
        assertTrue(Files.exists(Paths.get(localFilePath)));
        
        //Clean up
        Files.delete(Paths.get(localFilePath));
    }
    
    /**
     * Test of needsUpdate method, of class TopSitesUpdater.
     */
    @Test
    public void testAENeedsUpdate() throws IOException {
        System.out.println("needsUpdate");
        
        clearCache();
        
        TopSitesUpdater instance = new TopSitesUpdater();
        
        boolean expResult = true;
        boolean result = instance.needsUpdate();
        
        assertEquals(expResult, result);
        
        //Check after setting curent time (and hope there was no external update :) )
        instance.setInternalUpdateTime(NOW);
        
        expResult = false;        
        result = instance.needsUpdate();
        assertEquals(expResult, result);
    }

    /**
     * Test of run method, of class TopSitesUpdater.
     */
    public void testRun() {
        System.out.println("run");
        TopSitesUpdater instance = new TopSitesUpdater();
        instance.run();
    }
    
}
