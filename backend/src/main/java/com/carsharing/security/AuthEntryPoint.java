package com.carsharing.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author ITBird
 *
 */

@Component
public class AuthEntryPoint implements AuthenticationEntryPoint, Serializable {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // This is invoked when user tries to access a secured REST resource without supplying any credentials
        if (authException.getCause() instanceof EmailNotFoundException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}