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
 * @description RestExceptionMapper - general exception handler for web server
 * @package ie.peternagy.alexa.top.api.common.filter
 *
 */
package ie.peternagy.alexa.top.api.common.filter;

import ie.peternagy.alexa.top.api.common.exception.RestException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    HttpServletRequest request;

    @Override
    public Response toResponse(Throwable e) {

        
        int status;
        String jsonBody;
        if (e instanceof NotFoundException) {
            Logger.getLogger(RestExceptionMapper.class.getName()).log(Level.SEVERE, "NotFoundException: {0}", request.getRequestURL().toString());
            String location = String.format("Could not find resource for full path: %s", request.getRequestURL().toString());

            status = 404;
            jsonBody = String.format(RestException.ERROR_BASE_JSON, location, "api/request/handler/error", 404);

        } else if (e instanceof RestException) {
            Logger.getLogger(RestExceptionMapper.class.getName()).log(Level.SEVERE, null, e);
            status = ((RestException) e).getStatus();
            jsonBody = ((RestException) e).toJsonString();
        } else {
            Logger.getLogger(RestExceptionMapper.class.getName()).log(Level.SEVERE, null, e);
            status = 500;
            jsonBody = String.format(RestException.ERROR_BASE_JSON, "Internal server error", "api/request/handler/error", status);
        }

        return buildJsonErrorResponse(status, jsonBody);
    }

    /**
     * Json response builder shortcut
     * 
     * @param status
     * @param jsonBody
     * @return 
     */
    private Response buildJsonErrorResponse(int status, String jsonBody) {
        return Response
                .status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(jsonBody)
                .build();
    }
}
