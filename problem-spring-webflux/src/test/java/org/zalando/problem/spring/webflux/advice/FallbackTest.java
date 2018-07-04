package org.zalando.problem.spring.webflux.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import reactor.core.publisher.Mono;

import java.util.Optional;

final class FallbackTest implements AdviceTraitTesting {

    @Override
    public ProblemHandling unit() {
        return new FallbackProblemHandling();
    }

    @Test
    void customFallbackUsed() {
        webTestClient().get().uri("http://localhost/api/handler-problem")
                .accept(MediaType.TEXT_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaType.TEXT_XML)
                .expectHeader().valueEquals("X-Fallback-Used", "true");
    }

    @ControllerAdvice
    private static class FallbackProblemHandling implements ProblemHandling {

        @Override
        public Optional<MediaType> negotiate(final ServerWebExchange request) {
            return Optional.empty();
        }

        @Override
        public Mono<ResponseEntity<Problem>> fallback(final Throwable throwable, final Problem problem,
                final ServerWebExchange request, final HttpHeaders headers) {
            return Mono.just(ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .contentType(MediaType.TEXT_XML)
                    .header("X-Fallback-Used", Boolean.toString(true))
                    .body(null));
        }

    }

}
