package org.zalando.problem.spring.webflux.advice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import reactor.core.publisher.Mono;

public final class AdviceUtils {

    private AdviceUtils() {

    }

    public static Mono<Void> setHttpResponse(ResponseEntity<Problem> entity, ServerWebExchange exchange, ObjectMapper mapper) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(entity.getStatusCode());
        response.getHeaders().addAll(entity.getHeaders());
        try {
            DataBuffer buffer = response.bufferFactory()
                    .wrap(mapper.writeValueAsBytes(entity.getBody()));
            return response.writeWith(Mono.just(buffer))
                    .doOnError(error -> DataBufferUtils.release(buffer));
        } catch (JsonProcessingException ex) {
            return Mono.error(ex);
        }
    }
}
