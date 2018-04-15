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
 * @description SitesRedisExtractor - extract the alexa top data to redis
 * @package ie.peternagy.alexa.top.api.service
 */
package ie.peternagy.alexa.top.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ie.peternagy.alexa.top.api.cli.Environment;
import ie.peternagy.alexa.top.api.common.client.JedisFactory;
import ie.peternagy.alexa.top.api.resources.sites.model.Site;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class SitesRedisExtractor implements Runnable{

    private final String CACHE_KEY_SITE_LIST;
    private final String CACHE_KEY_TLD_LIST;
    private final ObjectMapper OBJECT_MAPPER;
    private final String UPDATE_KEY = "update";
    protected final BlockingQueue<Site> sites;
    protected volatile boolean stopProcessing = false;
    private SitesFileExtractor fileExtractor;
    protected volatile boolean isComplete = false;
    private int processedCnt = 0;
    private volatile boolean successfulUpdate = false;

    public SitesRedisExtractor(String DATA_FILE_PATH) {
        CACHE_KEY_SITE_LIST = String.format("%s:%s", Environment.getOrDefault("CACHE_LIST_SITES", "alexa:top:api:sites"), UPDATE_KEY);
        CACHE_KEY_TLD_LIST = String.format("%s:%s", Environment.getOrDefault("CACHE_LIST_BASE_TLD", "alexa:top:api:tld"), UPDATE_KEY);
        sites = new ArrayBlockingQueue<>(20000);
        OBJECT_MAPPER = new ObjectMapper();
        
        fileExtractor = new SitesFileExtractor(DATA_FILE_PATH, sites);
    }

    /**
     * Process the current queue by adding it to Redis
     * 
     */
    protected void storeQueue() {
        try (Jedis jedis = JedisFactory.getInstance().newClient()) {
            while(!stopProcessing && !isComplete){
                Site site = sites.take();
                //Check if processing should stop
                if(site instanceof SitePoison){
                    isComplete = true;
                    return;
                }
                String siteStr = OBJECT_MAPPER.writeValueAsString(site);
                
                jedis.rpush(CACHE_KEY_SITE_LIST, siteStr);
                jedis.rpush(String.format("%s:%s", CACHE_KEY_TLD_LIST, Site.hash(site.getTld())), siteStr);
                processedCnt++;
            }
        } catch (JedisConnectionException | InterruptedException | JsonProcessingException ex) {
            Logger.getLogger(SitesRedisExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        stopProcessing = true;
        fileExtractor.setStopProcessing(stopProcessing);
        
        isComplete = false;
    }
    
    /**
     * Replace the updated content with old content
     * 
     * @param isSuccess if false the update content is deleted
     */
    protected void cleanupRedis(boolean isSuccess){
        try(Jedis jedis = JedisFactory.getInstance().newClient()){
            Set<String> keys = jedis.keys(String.format("*:%s*", UPDATE_KEY));
            
            jedis.watch(keys.toArray(new String[1]));
            String replaceable = String.format(":%s", UPDATE_KEY);
            
            keys.forEach((key) -> {
                Transaction trans = jedis.multi();
                if(isSuccess){
                    String replaceWith = key.replace(replaceable, "");
                    trans.rename(key, replaceWith);
                }else{
                    trans.del(key);
                }
                
                trans.exec();
            });
        }
    }

    @Override
    public void run() {
        Logger.getLogger(SitesFileExtractor.class.getName()).log(Level.INFO, "======= Redis extractor started");
        isComplete = false;
        stopProcessing = false;
        
        Thread fileextractorThread = new Thread(fileExtractor);
        fileextractorThread.start();
        
        Thread redisQueueExecutorThread = new Thread(() -> {
            storeQueue();
        });
        redisQueueExecutorThread.start();
        
        
        while(!isComplete && !stopProcessing){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(SitesRedisExtractor.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        
        fileextractorThread.interrupt();
        
        try {
            fileextractorThread.join();
            redisQueueExecutorThread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(SitesRedisExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(fileExtractor.isIsComplete()){
            successfulUpdate = true;
            cleanupRedis(true);
            Logger.getLogger(SitesFileExtractor.class.getName()).log(Level.INFO, String.format("======= Redis extractor completed with success ==> processed {%d}", processedCnt));
        }else{
            successfulUpdate = false;
            cleanupRedis(false);
            Logger.getLogger(SitesFileExtractor.class.getName()).log(Level.INFO, String.format("======= Redis extractor completed with reset ==> processed {%d}", processedCnt));
        }
    }
    
    /**
     * Check if update process is complete
     * 
     * @return 
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Check if update was a success
     * 
     * @return 
     */
    public boolean isSuccessfulUpdate() {
        return successfulUpdate;
    }

    
    /**
     * Return the env CACHE_LIST_BASE_TLD
     * @return 
     */
    public static String getCacheKeyTldList(){
        return Environment.getOrDefault("CACHE_LIST_BASE_TLD", "alexa:top:api:tld");
    }
    
    /**
     * Return the cache key for a tld
     * 
     * {CACHE_LIST_BASE_TLD}:{hash}
     * 
     * @param tld
     * @return 
     */
    public static String getCacheKeyTldList(String tld){
        return String.format("%s:%s", Environment.getOrDefault("CACHE_LIST_BASE_TLD", "alexa:top:api:tld"), Site.hash(tld));
    }
    
    /**
     * Return the env CACHE_LIST_SITES
     * @return 
     */
    public static String getCacheKeySitesList(){
        return Environment.getOrDefault("CACHE_LIST_SITES", "alexa:top:api:sites");
    }
}
