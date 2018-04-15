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
 * @description SitesRedisExtractorTest - test cases for SitesRedisExtractor
 * @package ie.peternagy.alexa.top.api.service
 */
package ie.peternagy.alexa.top.api.service;

import ie.peternagy.alexa.top.api.cli.Environment;
import ie.peternagy.alexa.top.api.common.client.JedisFactory;
import ie.peternagy.alexa.top.api.resources.sites.model.Site;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import redis.clients.jedis.Jedis;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SitesRedisExtractorTest {

    private static TopSitesUpdater updater = new TopSitesUpdater();
    private static String filePath = Environment.getOrDefault("TOP_SITES_PATH", "/tmp/top-1m.csv.zip");

    public SitesRedisExtractorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        if (!Files.exists(Paths.get(filePath))) {
            if (!updater.downloadExternalData(updater.TMP_SITES_FILE_PATH)) {
                throw new RuntimeException("Failed to get external sites data");
            }

            filePath = updater.TMP_SITES_FILE_PATH;
        }
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        if (Files.exists(Paths.get(updater.TMP_SITES_FILE_PATH))) {
            Files.delete(Paths.get(updater.TMP_SITES_FILE_PATH));
        }
    }

    @Before
    public void setUp() {
        System.out.println("Running tests on: " + SitesRedisExtractor.class.getName());
    }

    /**
     * Test of storeQueue method, of class SitesRedisExtractor.
     */
    @Test
    public void testAAStoreQueue() {
        System.out.println("storeQueue");

        SitesRedisExtractor instance = new SitesRedisExtractor(filePath);
        boolean expResult = true;

        instance.sites.add(new SitePoison());
        instance.storeQueue();
        assertEquals(expResult, instance.isComplete());

        instance.isComplete = false;
        
        instance.sites.add(new Site(1, "google.com"));
        instance.sites.add(new SitePoison());
        instance.storeQueue();
        Jedis jedis = JedisFactory.getInstance().newClient();
        Set<String> keys = jedis.keys("*update*");

        assertTrue(!keys.isEmpty());

        instance.cleanupRedis(false);
    }
    
    /**
     * Test of storeQueue method, of class SitesRedisExtractor.
     */
    @Test
    public void testAAStoreQueueSuccess() {
        System.out.println("storeQueue");

        SitesRedisExtractor instance = new SitesRedisExtractor(filePath);
        
        instance.sites.add(new Site(1, "google.com"));
        instance.sites.add(new SitePoison());
        instance.storeQueue();
        Jedis jedis = JedisFactory.getInstance().newClient();
        Set<String> keys = jedis.keys("*update*");

        assertTrue(!keys.isEmpty());
        
        instance.cleanupRedis(true);
        
        keys = jedis.keys("*update*");

        assertTrue(keys.isEmpty());
    }

    /**
     * Test of cleanupRedis method, of class SitesRedisExtractor.
     */
    @Test
    public void testABCleanupRedis() throws InterruptedException {
        System.out.println("cleanupRedis");

        boolean isSuccess = false;
        SitesRedisExtractor instance = new SitesRedisExtractor(filePath);
        instance.sites.add(new Site(1, "google.com"));

        new Thread(() -> {
            instance.storeQueue();
        }).start();
        
        Thread.sleep(100);
        Jedis jedis = JedisFactory.getInstance().newClient();
        Set<String> keys = jedis.keys("*update*");

        assertTrue(!keys.isEmpty());

        instance.cleanupRedis(isSuccess);

        Thread.sleep(1000);
        keys = jedis.keys("*update*");
        assertTrue(keys.isEmpty());
    }

    /**
     * Test of run method, of class SitesRedisExtractor.
     */
    @Test
    public void testACRun() throws InterruptedException {
        System.out.println("run");

        SitesRedisExtractor instance = new SitesRedisExtractor(filePath);
        Thread t = new Thread(instance);
        t.start();
        
        int i = 2;
        while(instance.sites.isEmpty() && ++i < 12){
            Thread.sleep((long) Math.pow(2, i));
        }
        
        
        instance.stopProcessing = true;
        
        t.interrupt();
        t.join();
        
        Jedis jedis = JedisFactory.getInstance().newClient();
        Set<String> keys = jedis.keys("*update*");

        assertTrue(keys.isEmpty());
    }

}
