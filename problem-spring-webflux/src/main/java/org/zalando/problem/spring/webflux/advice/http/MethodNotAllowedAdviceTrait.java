package org.zalando.problem.spring.webflux.advice.http;

import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.Set;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public interface MethodNotAllowedAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleRequestMethodNotSupportedException(
            final MethodNotAllowedException exception,
            final ServerWebExchange request) {

        @Nullable final Set<HttpMethod> methods = exception.getSupportedMethods();

        if (methods.isEmpty()) {
            return create(Status.METHOD_NOT_ALLOWED, exception, request);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setAllow(methods);

        return create(Status.METHOD_NOT_ALLOWED, exception, request, headers);
    }

}
