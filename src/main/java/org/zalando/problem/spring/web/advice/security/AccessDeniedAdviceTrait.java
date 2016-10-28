package org.zalando.problem.spring.web.advice.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response;

/**
 * The request was a valid request, but the server is refusing to respond to it. The user might be logged in but does
 * not have the necessary permissions for the resource.
 */
public interface AccessDeniedAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleAccessDenied(final AccessDeniedException e,
            final NativeWebRequest request) {
        return create(Response.Status.FORBIDDEN, e, request);
    }

}
