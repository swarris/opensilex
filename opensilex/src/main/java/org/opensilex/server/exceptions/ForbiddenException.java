/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.server.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.opensilex.server.response.ErrorResponse;

public class ForbiddenException extends WebApplicationException {

    public ForbiddenException(String message) {
        super(Response.status(Response.Status.FORBIDDEN)
                .entity(new ErrorResponse(
                        Response.Status.FORBIDDEN,
                        "Access denied",
                        message))
                .type(MediaType.APPLICATION_JSON)
                .build()
        );
    }

}
