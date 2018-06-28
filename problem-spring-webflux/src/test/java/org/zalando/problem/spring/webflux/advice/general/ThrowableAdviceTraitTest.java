package org.zalando.problem.spring.webflux.advice.general;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

final class ThrowableAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void throwable() {
        webTestClient().get().uri("http://localhost/api/handler-throwable")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody()
                .jsonPath("$.type").doesNotExist()
                .jsonPath("$.title").isEqualTo("Internal Server Error")
                .jsonPath("$.status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.detail").isEqualTo("expected")
                .jsonPath("$.stacktrace").doesNotExist()
                .jsonPath("$.cause").doesNotExist();
    }

    @Test
    void annotatedThrowable() {
        webTestClient().get().uri("http://localhost/api/handler-throwable-annotated")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_IMPLEMENTED)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody()
                .jsonPath("$.type").doesNotExist()
                .jsonPath("$.title").isEqualTo("Not Implemented")
                .jsonPath("$.status").isEqualTo(HttpStatus.NOT_IMPLEMENTED.value())
                .jsonPath("$.stacktrace").doesNotExist()
                .jsonPath("$.cause").doesNotExist();
    }

    @Test
    void annotatedWithReasonThrowable() {
        webTestClient().get().uri("http://localhost/api/handler-throwable-annotated-reason")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_IMPLEMENTED)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody()
                .jsonPath("$.type").doesNotExist()
                .jsonPath("$.title").isEqualTo("Test reason")
                .jsonPath("$.status").isEqualTo(HttpStatus.NOT_IMPLEMENTED.value())
                .jsonPath("$.stacktrace").doesNotExist()
                .jsonPath("$.cause").doesNotExist();
    }

}
