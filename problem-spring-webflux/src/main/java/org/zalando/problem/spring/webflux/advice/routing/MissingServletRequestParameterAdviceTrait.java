package org.zalando.problem.spring.webflux.advice.routing;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see MissingServletRequestParameterException
 * @see Status#BAD_REQUEST
 */
@API(status = STABLE)
public interface MissingServletRequestParameterAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException exception,
            final ServerWebExchange request) {
        return create(Status.BAD_REQUEST, exception, request);
    }

}
