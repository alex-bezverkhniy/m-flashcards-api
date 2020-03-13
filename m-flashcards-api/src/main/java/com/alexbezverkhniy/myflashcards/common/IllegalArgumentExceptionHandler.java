package com.alexbezverkhniy.myflashcards.common;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionHandler implements ExceptionMapper<IllegalArgumentException> {
    /**
     * Map an exception to a {@link javax.ws.rs.core.Response}. Returning
     * {@code null} results in a {@link javax.ws.rs.core.Response.Status#NO_CONTENT}
     * response. Throwing a runtime exception results in a
     * {@link javax.ws.rs.core.Response.Status#INTERNAL_SERVER_ERROR} response.
     *
     * @param exception the exception to map to a response.
     * @return a response mapped from the supplied exception.
     */
    @Override
    public Response toResponse(final IllegalArgumentException exception) {
        return Response.status(405, exception.getMessage()).build();
    }
}
