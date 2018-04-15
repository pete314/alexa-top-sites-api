/* * 
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
 * @description SitesRequest - SHORT DESCRIPTION
 * @package    ie.peternagy.alexa.top.api.resources.sites.model
 * 
 */
package ie.peternagy.alexa.top.api.resources.sites.model;

import ie.peternagy.alexa.top.api.common.request.PaginatedRequest;
import java.util.Objects;
import javax.ws.rs.core.MultivaluedMap;

public class SitesRequest extends PaginatedRequest{
    private static final String[] FIELDS = new String[]{"tld"};
    private String tld;
    private String site;
    
    public SitesRequest(String[] allowedFields, MultivaluedMap<String, String> params) {
        super(allowedFields, params);
        init(params);
    }
    
    public SitesRequest(MultivaluedMap<String, String> params) {
        this(FIELDS, params);
    }
    
    /**
     * Initalize current properties
     * 
     * @param params 
     */
    private void init(MultivaluedMap<String, String> params){
        for (String param : params.keySet()) {
            if (ALLOWED_FIELDS.contains(param)) {
                switch (param) {
                    case "tld":
                        tld = params.getFirst(param);
                        break;
                    case "site":
                        site = params.getFirst(param);
                        break;
                }
            }else{
                addError(param, "Parameter not allowed");
            }
        }
    }

    public static String[] getFIELDS() {
        return FIELDS;
    }
    
    public String getTld() {
        return tld;
    }

    public String getSite() {
        return site;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.tld);
        hash = 59 * hash + Objects.hashCode(this.site);
        
        hash = 47 * hash + super.hashCode();
        
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SitesRequest other = (SitesRequest) obj;
        if (!Objects.equals(this.tld, other.tld)) {
            return false;
        }
        if (!Objects.equals(this.site, other.site)) {
            return false;
        }
        
        if(obj instanceof PaginatedRequest){
            return super.equals(obj);
        }
        
        return false;
        
    }
    
    
    
    
    
}
