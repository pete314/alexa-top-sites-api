/** 
 * Copyright (C) 2018 Peter Nagy (https://peternagy.ie)
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
 * =========================================================
 * @author     Peter Nagy <pnagy@alison.com>
 * @since      03, 2018
 * @version    0.1
 * @description SitesDataMapper - handles data requests
 * @package    ie.peternagy.alexa.top.api.resources.sites.model
 * 
 */
package ie.peternagy.alexa.top.api.resources.sites.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ie.peternagy.alexa.top.api.common.client.JedisFactory;
import ie.peternagy.alexa.top.api.common.exception.RestException;
import ie.peternagy.alexa.top.api.service.SitesRedisExtractor;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class SitesDataMapper {
    private final JedisFactory JEDIS_FACTORY;
    private final ObjectMapper OBJECT_MAPPER;

    public SitesDataMapper() {
        JEDIS_FACTORY = JedisFactory.getInstance();
        OBJECT_MAPPER = new ObjectMapper();
    }
    
    /**
     * Get list of sites with TLD 
     * 
     * @param tld - eg: com, eu, ie, hu etc
     * @param page - the offset to start from
     * @param size - the number of items to get
     * @return
     * @throws RestException 
     */
    public List<Site> getTldSiteList(String tld, int page, int size) throws RestException{
        return getSiteListItems(SitesRedisExtractor.getCacheKeyTldList(tld), page, size);
    }
    
    /**
     * Get the top ranked sites list chunk
     * 
     * @param page - the offset to start from
     * @param size - the number of items to get
     * @return
     * @throws RestException 
     */
    public List<Site> getRankedSitesList(int page, int size) throws RestException{
        return getSiteListItems(SitesRedisExtractor.getCacheKeySitesList(), page, size);
    }
    
    
    /**
     * Get list items with Site json string
     * 
     * @param cacheKey - the list key name
     * @param page - the offset (*size) to start from
     * @param size - the number of items to get
     * @return
     * @throws RestException 
     */
    protected List<Site> getSiteListItems(String cacheKey, int page, int size) throws RestException{
        try(Jedis jedis = JEDIS_FACTORY.newClient()){
            if(jedis.exists(cacheKey)){
                long start = (page - 1) * size;//page is always at least 1
                long end = page * size;
                
                List<String> jsonStrList = jedis.lrange(cacheKey, start, end);
                if(jsonStrList != null && !jsonStrList.isEmpty()){
                    return transformList(jsonStrList);
                }
                
            }
        }catch(JedisConnectionException ex){
            throw new RestException("api/sites/storage/cache","Error while retreiving data", 503, 503, ex);
        }catch(RuntimeException ex){
            throw new RestException("api/sites/storage/cache","Error while retreiving data", 503, 503, ex);
        }
        
        throw new RestException("api/sites/request/verify","Data not found", 404, 404);
    }
    
    /**
     * Transform Json Strings list to Site list
     * 
     * @param jsonStrList
     * @return 
     */
    protected List<Site> transformList(List<String> jsonStrList) throws RuntimeException{
        return jsonStrList.parallelStream().map(tmp -> {
                    try {
                        return OBJECT_MAPPER.readValue(tmp, Site.class);
                    } catch (IOException ex) {
                        Logger.getLogger(SitesDataMapper.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException(ex);
                    }
                }).collect(Collectors.toList());
    }
    
    /**
     * Get the total list size for a TLD
     * 
     * @param tld
     * @return
     * @throws RestException 
     */
    public int getTldListTotal(String tld) throws RestException{
        return getListSize(SitesRedisExtractor.getCacheKeyTldList(tld));
    }
    
    /**
     * Get sites ranked total list size
     * 
     * @return
     * @throws RestException 
     */
    public int getSitesRankListTotal() throws RestException{
        return getListSize(SitesRedisExtractor.getCacheKeySitesList());
    }
    
    /**
     * Get the total number of items in a list
     * 
     * @param listKey - the list tot lookup
     * @return
     * @throws RestException 
     */
    protected int getListSize(String listKey) throws RestException{
        try(Jedis jedis = JEDIS_FACTORY.newClient()){
            if(jedis.exists(listKey)){
                return Math.toIntExact(jedis.llen(listKey));
            }
        }catch(JedisConnectionException ex){
            throw new RestException("api/sites/storage/cache","Error while retreiving data", 503, 503, ex);
        }
        
        throw new RestException("api/sites/request/verify","Item not found", 404, 404);
    }
}
