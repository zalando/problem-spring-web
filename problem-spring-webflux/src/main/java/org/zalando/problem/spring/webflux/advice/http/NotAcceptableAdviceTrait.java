package org.zalando.problem.spring.webflux.advice.http;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see NotAcceptableStatusException
 * @see Status#NOT_ACCEPTABLE
 */
@API(status = STABLE)
public interface NotAcceptableAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleMediaTypeNotAcceptable(
            final NotAcceptableStatusException exception,
            final ServerWebExchange request) {
        return create(Status.NOT_ACCEPTABLE, exception, request);
    }

}
