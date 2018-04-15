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
 * @description PaginatedRequest - request type wrapping the parameters to enable paginations
 * @package    ie.peternagy.alexa.top.api.common.request
 * 
 */
package ie.peternagy.alexa.top.api.common.request;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang3.ArrayUtils;

public abstract class PaginatedRequest {
    protected final Set<String> ALLOWED_FIELDS;
    private int size = 50;
    private int page = 1;
    protected Set<Object> errors;
    
    public PaginatedRequest(String[] allowedFields, MultivaluedMap<String, String> params) {
        ALLOWED_FIELDS = new HashSet<>(
                Arrays.asList(
                        ArrayUtils.addAll(new String[]{"size", "page"}, allowedFields)));
        init(params);
    }
    
    /**
     * Initialize the request 
     * 
     * Note: this includes strict validation against allowed fields
     * @param params should come from UriInfo
     */
    private void init(MultivaluedMap<String, String> params){
        for (String param : params.keySet()) {
            if (ALLOWED_FIELDS.contains(param)) {
                switch (param) {
                    case "size":
                        try{
                            size = Integer.parseInt(params.getFirst(param));
                            if(size <= 0 || size > 1000){
                                addError(param, "Invalid parameter value, looking for more than 0 and less than 1000");
                            }
                        }catch(NumberFormatException ex){
                            addError(param, "Invalid parameter type, looking for integer");
                        }
                        break;
                    case "page":
                        try{
                            page = Integer.parseInt(params.getFirst(param));
                            if(page <= 0){
                                addError(param, "Invalid parameter value, looking for more than 0");
                            }
                        }catch(NumberFormatException ex){
                            addError(param, "Invalid parameter type, looking for integer");
                        }
                        
                        break;
                }
            }else{
                addError(param, "Parameter not allowed");
            }
        }
    }
    
    /**
     * Add error to wrong parameter list
     * 
     * @param param - the parameter name with error
     * @param error - the error description 
     */
    protected void addError(String param, String error){
        if(null == errors){
            errors = new HashSet<>();
        }
        errors.add(String.format("%s: %s", param, error));
    }

    public boolean isValid() {
        return errors == null || errors.isEmpty();
    }

    public Set<Object> getErrors() {
        return errors;
    }
    
    public Set<String> getALLOWED_FIELDS() {
        return ALLOWED_FIELDS;
    }
    
    public int getSize() {
        return size;
    }

    public int getPage() {
        return page;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.ALLOWED_FIELDS);
        hash = 97 * hash + this.size;
        hash = 97 * hash + this.page;
        hash = 97 * hash + Objects.hashCode(this.errors);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
System.out.println("f1");
        if (obj == null) {
            return false;
        }
System.out.println("f2");
        if (getClass() != obj.getClass()) {
            return false;
        }
System.out.println("f3");
        final PaginatedRequest other = (PaginatedRequest) obj;
        if (this.size != other.size) {
            return false;
        }
System.out.println("f4");
        if (this.page != other.page) {
            return false;
        }
System.out.println("f5");
        if (!Objects.equals(this.ALLOWED_FIELDS, other.ALLOWED_FIELDS)) {
            return false;
        }
System.out.println("f6");
        if (!Objects.equals(this.errors, other.errors)) {
            return false;
        }

        return true;
    }
    
    
}
