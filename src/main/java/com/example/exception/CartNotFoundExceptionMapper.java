/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class CartNotFoundExceptionMapper implements
        ExceptionMapper<CartNotFoundException>{
    private static final Logger logger = LoggerFactory.getLogger(CartNotFoundExceptionMapper.class);
    
     @Override
    public Response toResponse(CartNotFoundException exception) {
        logger.error("Cart not found: {}", exception.getMessage(), exception);
        return Response.status(Response.Status.NOT_FOUND)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
    
}
