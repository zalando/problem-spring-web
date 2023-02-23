package org.zalando.problem.spring.webflux.advice.network;

import dev.failsafe.CircuitBreakerOpenException;
import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = EXPERIMENTAL)
public interface CircuitBreakerOpenAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleCircuitBreakerOpen(
            final CircuitBreakerOpenException exception,
            final ServerWebExchange request) {

        final long delay = exception.getCircuitBreaker()
                .getRemainingDelay().getSeconds();
        final HttpHeaders headers = retryAfter(delay);
        return create(Status.SERVICE_UNAVAILABLE, exception, request, headers);
    }

    default HttpHeaders retryAfter(final long delay) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.RETRY_AFTER, String.valueOf(delay));
        return headers;
    }

}
