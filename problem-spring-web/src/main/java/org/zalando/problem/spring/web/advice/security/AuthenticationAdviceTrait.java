package org.zalando.problem.spring.web.advice.security;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.zalando.problem.Status.UNAUTHORIZED;

/**
 * Similar to 403 Forbidden, but specifically for use when authentication is required and has failed or has not yet
 * been provided.
 */
@API(status = STABLE)
public interface AuthenticationAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default ResponseEntity<Problem> handleAuthentication(final AuthenticationException e,
            final NativeWebRequest request) {
        return create(UNAUTHORIZED, e, request);
    }

}
