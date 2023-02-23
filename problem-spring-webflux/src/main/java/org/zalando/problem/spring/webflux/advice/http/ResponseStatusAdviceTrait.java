package org.zalando.problem.spring.webflux.advice.http;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;
import org.zalando.problem.spring.webflux.advice.SpringAdviceTrait;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see ResponseStatusException
 */
@API(status = STABLE)
public interface ResponseStatusAdviceTrait extends SpringAdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleResponseStatusException(
            final ResponseStatusException exception,
            final ServerWebExchange request) {

        return create(new HttpStatusAdapter(exception.getStatusCode()), exception, request);
    }

}
