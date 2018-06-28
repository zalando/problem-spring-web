package org.zalando.problem.spring.webflux.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler;
import org.springframework.web.server.*;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.webflux.advice.http.HttpAdviceTrait;
import reactor.core.publisher.Mono;

public class ProblemExceptionHandler extends WebFluxResponseStatusExceptionHandler {

    private final ObjectMapper mapper;

    private final HttpAdviceTrait advice;

    ProblemExceptionHandler(ObjectMapper mapper, HttpAdviceTrait advice) {
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
            return entityMono
                    .flatMap(response -> {
                        exchange.getResponse().setStatusCode(response.getStatusCode());
                        exchange.getResponse().getHeaders().addAll(response.getHeaders());
                        try {
                            return exchange.getResponse()
                                    .writeWith(Mono.just(new DefaultDataBufferFactory().wrap(mapper.writeValueAsBytes(response.getBody()))));
                        } catch (JsonProcessingException e) {
                            return Mono.error(throwable);
                        }
                    });
        }
        return Mono.error(throwable);
    }

}
