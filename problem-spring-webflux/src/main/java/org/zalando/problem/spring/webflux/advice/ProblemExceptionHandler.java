package org.zalando.problem.spring.webflux.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.*;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.webflux.advice.http.HttpAdviceTrait;
import org.zalando.problem.spring.webflux.advice.utils.AdviceUtils;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;

public class ProblemExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper mapper;
    private final HttpAdviceTrait advice;

    public ProblemExceptionHandler(final ObjectMapper mapper, final HttpAdviceTrait advice) {
        this.mapper = mapper;
        this.advice = advice;
    }

    @Nonnull
    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable throwable) {
        if (throwable instanceof ResponseStatusException) {
            final Mono<ResponseEntity<Problem>> entityMono = find(exchange, throwable);
            return entityMono.flatMap(entity -> AdviceUtils.setHttpResponse(entity, exchange, mapper));
        }
        return Mono.error(throwable);
    }

    private Mono<ResponseEntity<Problem>> find(final ServerWebExchange exchange, final Throwable throwable) {
        if (throwable instanceof NotAcceptableStatusException) {
            return advice.handleMediaTypeNotAcceptable((NotAcceptableStatusException) throwable, exchange);
        } else if (throwable instanceof UnsupportedMediaTypeStatusException) {
            return advice.handleMediaTypeNotSupportedException((UnsupportedMediaTypeStatusException) throwable, exchange);
        } else if (throwable instanceof MethodNotAllowedException) {
            return advice.handleRequestMethodNotSupportedException((MethodNotAllowedException) throwable, exchange);
        } else {
            return advice.handleResponseStatusException((ResponseStatusException) throwable, exchange);
        }
    }

}
