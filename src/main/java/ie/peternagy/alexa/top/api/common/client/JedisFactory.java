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
 * @description JedisFactory - Jedis client factory
 * @package ie.peternagy.alexa.top.api.common.client
 */
package ie.peternagy.alexa.top.api.common.client;

import ie.peternagy.alexa.top.api.cli.Environment;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class JedisFactory {
    private static final Object LOCK = new Object();
    private static JedisFactory instance;
    private JedisPool jedisPool;
    
    private JedisFactory(){
        reInitalizePool();
    }
    
    /**
     * Get a singleton instance of JedisFactory
     * 
     * @return 
     */
    public static JedisFactory getInstance(){
        if(instance == null){
            synchronized(LOCK){
                instance = new JedisFactory();
            }
        }
        
        return instance;
    }
    
    /**
     * Get new Jedis client(resource) from pool
     * 
     * @return 
     */
    public Jedis newClient(){
        return jedisPool.getResource();
    }
    
    /**
     * Check if the underlying pool is closed
     * 
     * @return 
     */
    public boolean isClosed(){
        return jedisPool.isClosed();
    }
    
    /**
     * Reset the current pool
     */
    public void reInitalizePool(){
        jedisPool = new JedisPool(getDefaultConfig(), Environment.getOrDefault("REDIS_MASTER_HOST", "127.0.0.1"), Environment.getOrDefault("REDIS_MASTER_PORT", 6379), 10000);
    }

    /**
     * Get the default configuration for localhost connection
     *
     * @return Redisson/Config
     */
    private static JedisPoolConfig getDefaultConfig() {
        final JedisPoolConfig jpc = new JedisPoolConfig();
        jpc.setTestOnBorrow(true);
        jpc.setMinEvictableIdleTimeMillis(6000);
        jpc.setSoftMinEvictableIdleTimeMillis(7000);
        jpc.setNumTestsPerEvictionRun(10);
        jpc.setTestOnReturn(true);
        jpc.setTestWhileIdle(true);
        jpc.setTimeBetweenEvictionRunsMillis(10000);
        jpc.setMaxTotal(1280);
        jpc.setMinIdle(32);
        jpc.setBlockWhenExhausted(false);
        

        return jpc;
    }
}
