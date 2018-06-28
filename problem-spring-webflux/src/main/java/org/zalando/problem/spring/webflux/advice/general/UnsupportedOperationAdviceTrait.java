package org.zalando.problem.spring.webflux.advice.general;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see UnsupportedOperationException
 * @see Status#NOT_IMPLEMENTED
 */
@API(status = STABLE)
public interface UnsupportedOperationAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleUnsupportedOperation(
            final UnsupportedOperationException exception,
            final ServerWebExchange request) {
        return create(Status.NOT_IMPLEMENTED, exception, request);
    }

}
