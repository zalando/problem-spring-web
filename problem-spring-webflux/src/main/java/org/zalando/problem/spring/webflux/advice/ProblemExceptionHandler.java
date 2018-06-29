package org.zalando.problem.spring.webflux.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.*;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.webflux.advice.http.HttpAdviceTrait;
import org.zalando.problem.spring.webflux.advice.utils.AdviceUtils;
import reactor.core.publisher.Mono;

public class ProblemExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper mapper;

    private final HttpAdviceTrait advice;

    public ProblemExceptionHandler(ObjectMapper mapper, HttpAdviceTrait advice) {
        this.mapper = mapper;
        this.advice = advice;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        Mono<ResponseEntity<Problem>> entityMono;
        if (throwable instanceof ResponseStatusException) {
            if (throwable instanceof NotAcceptableStatusException) {
                entityMono = advice.handleMediaTypeNotAcceptable((NotAcceptableStatusException) throwable, exchange);
            } else if (throwable instanceof UnsupportedMediaTypeStatusException) {
                entityMono = advice.handleMediaTypeNotSupportedException((UnsupportedMediaTypeStatusException) throwable, exchange);
            } else if (throwable instanceof MethodNotAllowedException) {
                entityMono = advice.handleRequestMethodNotSupportedException((MethodNotAllowedException) throwable, exchange);
            } else {
                entityMono = advice.handleResponseStatusException((ResponseStatusException) throwable, exchange);
            }
            return entityMono.flatMap(entity -> AdviceUtils.setHttpResponse(entity, exchange, mapper));
        }
        return Mono.error(throwable);
    }

}
