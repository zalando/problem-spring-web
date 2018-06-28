package org.zalando.problem.spring.webflux.advice.security;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;
import static org.zalando.problem.Status.FORBIDDEN;

/**
 * The request was a valid request, but the server is refusing to respond to it. The user might be logged in but does
 * not have the necessary permissions for the resource.
 */
@API(status = STABLE)
public interface AccessDeniedAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleAccessDenied(final AccessDeniedException e,
            final ServerWebExchange request) {
        return create(FORBIDDEN, e, request);
    }

}
