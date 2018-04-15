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
 * @description SitesResource - the endpoint handling sites data requests
 * @package ie.peternagy.alexa.top.api.resources.sites.resource
 */
package ie.peternagy.alexa.top.api.resources.sites.resource;

import ie.peternagy.alexa.top.api.common.response.PaginatedResponse;
import ie.peternagy.alexa.top.api.resources.sites.model.SitesRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.jboss.resteasy.annotations.cache.Cache;

@Path("/v0.1/alexa-top")
public class SitesResource {
    
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Cache(maxAge=1800, mustRevalidate = false, noStore = false, proxyRevalidate = false, sMaxAge = 1800)
    public Response getTopSites(@Context UriInfo uriInfo) throws Throwable{
        SitesRequest sitesRequest = new SitesRequest(uriInfo.getQueryParameters());
        if(!sitesRequest.isValid()){
            return Response.status(Response.Status.BAD_REQUEST).entity(sitesRequest.getErrors()).build();
        }
        
        PaginatedResponse paginatedResponse = SitesController.getInstance().getTopSites(sitesRequest);
        paginatedResponse.buildPaginationLinks(uriInfo);
        
        return Response.ok(paginatedResponse).type(MediaType.APPLICATION_JSON).build();
    }
}
