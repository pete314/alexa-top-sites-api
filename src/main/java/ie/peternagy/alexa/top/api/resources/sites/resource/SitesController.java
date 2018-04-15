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
 *
 * @author Peter Nagy <pnagy@alison.com>
 * @since 03, 2018
 * @version 0.1
 * @description SitesController - controller for Sites
 * @package ie.peternagy.alexa.top.api.resources.sites.resource
 *
 */
package ie.peternagy.alexa.top.api.resources.sites.resource;

import com.google.common.base.Strings;
import ie.peternagy.alexa.top.api.common.exception.RestException;
import ie.peternagy.alexa.top.api.common.response.PaginatedResponse;
import ie.peternagy.alexa.top.api.resources.sites.model.Site;
import ie.peternagy.alexa.top.api.resources.sites.model.SitesDataMapper;
import ie.peternagy.alexa.top.api.resources.sites.model.SitesRequest;
import java.util.ArrayList;
import java.util.List;

public class SitesController {
    private final SitesDataMapper SITES_DATA_MAPPER;
    
    private SitesController() {
        SITES_DATA_MAPPER = new SitesDataMapper();
    }

    private static class SitesControllerHolder {
        public static final SitesController instance = new SitesController();
    }

    /**
     * Get a thread-safe singleton instance of this
     * 
     * @return 
     */
    public static SitesController getInstance() {
        return SitesControllerHolder.instance;
    }
    
    /**
     * Get top sites by request
     * 
     * @param sitesRequest - initalized and verified by caller
     * @return
     * @throws RestException 
     */
    public PaginatedResponse getTopSites(SitesRequest sitesRequest) throws RestException{
        int total;
        List<Site> items;
        
        if(!Strings.isNullOrEmpty(sitesRequest.getTld())){
            total = SITES_DATA_MAPPER.getTldListTotal(sitesRequest.getTld());
            items = SITES_DATA_MAPPER.getTldSiteList(sitesRequest.getTld(), sitesRequest.getPage(), sitesRequest.getSize());
        }else{
            total = SITES_DATA_MAPPER.getSitesRankListTotal();
            items = SITES_DATA_MAPPER.getRankedSitesList(sitesRequest.getPage(), sitesRequest.getSize());
        }
        
        return buildPaginatedResponse(sitesRequest, total, items);
    }
    
    /**
     * Build paginated response
     * 
     * Note: this handles overflowing requests, and sets previous to last page with entries
     * 
     * @param sitesRequest
     * @param total
     * @param items
     * @return 
     */
    protected PaginatedResponse buildPaginatedResponse(SitesRequest sitesRequest, int total, List<Site> items){
        Integer prev = null;
        Integer next = null;
        if(sitesRequest.getPage() > 1){
            if(total > sitesRequest.getPage() * sitesRequest.getSize()){
                prev = sitesRequest.getPage() -1;
            }else{
                prev = total / sitesRequest.getSize();
            }
        }
        
        if(total > sitesRequest.getPage() * sitesRequest.getSize()){
            next = sitesRequest.getPage() + 1;
        }
        
        return new PaginatedResponse(total, new ArrayList<>(items), next, prev);
    }
    
    
}
