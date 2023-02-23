package org.zalando.problem.spring.web.advice.security;

import org.apiguardian.api.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * A compound {@link AuthenticationEntryPoint}, {@link AuthenticationFailureHandler} and {@link AccessDeniedHandler}
 * that delegates exceptions to Spring WebMVC's {@link HandlerExceptionResolver} as defined in {@link WebMvcConfigurationSupport}.
 *
 * Compatible with spring-webmvc 4.3.3.
 */
@API(status = STABLE)
@Component
public class SecurityProblemSupport implements AuthenticationEntryPoint, AuthenticationFailureHandler, AccessDeniedHandler {

    private final HandlerExceptionResolver resolver;

    @API(status = INTERNAL)
    @Autowired
    public SecurityProblemSupport(
            @Qualifier("handlerExceptionResolver") final HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
            final AuthenticationException exception) {
        resolver.resolveException(request, response, null, exception);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    		AuthenticationException exception) throws IOException, ServletException {
    	resolver.resolveException(request, response, null, exception);
    }

    @Override
    public void handle(final HttpServletRequest request, final HttpServletResponse response,
            final AccessDeniedException exception) {
        resolver.resolveException(request, response, null, exception);
    }

}
