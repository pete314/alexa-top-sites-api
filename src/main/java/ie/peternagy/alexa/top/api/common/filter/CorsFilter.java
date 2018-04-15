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
 * @description CorsFilter - handles preflight (OPTIONS) requests
 * @package    ie.peternagy.alexa.top.api.common.filter
 * 
 */
package ie.peternagy.alexa.top.api.common.filter;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.spi.CorsHeaders;

@PreMatching
public class CorsFilter implements ContainerRequestFilter{

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if(requestContext.getMethod().equalsIgnoreCase("options") || requestContext.getMethod().equalsIgnoreCase("option")){
            handlePreflight(requestContext);
        }
    }
 
    /**
     * Handle the preflight requests
     * 
     * @param requestContext
     * @throws IOException 
     */
    private void handlePreflight(ContainerRequestContext requestContext) throws IOException {
        final Response.ResponseBuilder response = Response.ok();
        
        String requestHeaders = requestContext.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
        String requestMethods = requestContext.getHeaderString(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD);
        
        if (requestHeaders != null) {
            response.header(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders);
        }else{
            response.header(CorsHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "x-requested-with, Content-Type, origin, authorization, accept, client-security-token");
        }

        if (requestMethods != null) {
            response.header(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethods);
        }else{
            response.header(CorsHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, DELETE, PUT, PATCH");
        }

        response.header(CorsHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.header(CorsHeaders.ACCESS_CONTROL_MAX_AGE, 600);
        
        requestContext.abortWith(response.build());
    }
}
