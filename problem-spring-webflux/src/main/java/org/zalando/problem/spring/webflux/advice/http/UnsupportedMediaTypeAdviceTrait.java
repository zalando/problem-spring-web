package org.zalando.problem.spring.webflux.advice.http;

import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see UnsupportedMediaTypeStatusException
 * @see Status#UNSUPPORTED_MEDIA_TYPE
 */
@API(status = STABLE)
public interface UnsupportedMediaTypeAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleMediaTypeNotSupportedException(
            final UnsupportedMediaTypeStatusException exception,
            final ServerWebExchange request) {

        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(exception.getSupportedMediaTypes());

        return create(Status.UNSUPPORTED_MEDIA_TYPE, exception, request, headers);
    }

}
