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
 * @description PaginatedResponse - paginated response
 * @package    ie.peternagy.alexa.top.api.common.response
 * 
 */
package ie.peternagy.alexa.top.api.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import java.util.List;
import javax.ws.rs.core.UriInfo;

public class PaginatedResponse {
    private int total;
    private String next;
    private String previous;
    private List<Object> results;
    
    @JsonIgnore
    private Integer nextPage;
    @JsonIgnore
    private Integer prevPage;

    public PaginatedResponse() {
    }

    public PaginatedResponse(int total, List<Object> results) {
        this.total = total;
        this.results = results;
    }
    
    public PaginatedResponse(int total, List<Object> results, Integer nextPage, Integer prevPage) {
        this.total = total;
        this.results = results;
        this.nextPage = nextPage;
        this.prevPage = prevPage;
    }
    
    public PaginatedResponse(int total, List<Object> results, String next, String previous) {
        this.total = total;
        this.results = results;
        this.next = next;
        this.previous = previous;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Object> getResults() {
        return results;
    }

    public void setResults(List<Object> results) {
        this.results = results;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
    
    /**
     * Build links for paginated response, HATEOAS
     * 
     * @param uriInfo 
     */
    public void buildPaginationLinks(UriInfo uriInfo){
        String path = uriInfo.getPath();
        if(uriInfo.getQueryParameters().containsKey("page")){
            int currentPage = Integer.parseInt(uriInfo.getQueryParameters().getFirst("page"));
            if(prevPage != null){
                previous = path + "?" + uriInfo.getRequestUri().getQuery().replaceFirst(String.format("page=%d", currentPage), String.format("page=%d", prevPage));
            }
            if(nextPage != null){
                next = path + "?" + uriInfo.getRequestUri().getQuery().replaceFirst(String.format("page=%d", currentPage), String.format("page=%d", nextPage));
            }
        }else{
            String query = Strings.isNullOrEmpty(uriInfo.getRequestUri().getQuery()) ? "?":  "?" + uriInfo.getRequestUri().getQuery() + "&";
            if(prevPage != null){
                previous = path + String.format("%s%s", query, String.format("page=%d&", prevPage));
            }
            if(nextPage != null){
                next = path + String.format("%s%s", query, String.format("page=%d", nextPage));
            }
        }
    }
    
}
