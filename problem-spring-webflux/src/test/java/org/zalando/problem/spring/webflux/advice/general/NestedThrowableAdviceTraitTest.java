package org.zalando.problem.spring.webflux.advice.general;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;

final class NestedThrowableAdviceTraitTest implements AdviceTraitTesting {

    @Override
    public ProblemHandling unit() {
        return new NestedProblemHandling();
    }

    @Override
    public ObjectMapper mapper() {
        return new ObjectMapper().registerModule(new ProblemModule().withStackTraces());
    }

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
                .jsonPath("$.cause").exists();
    }

    @Test
    void nestedThrowable() {
        webTestClient().get().uri("http://localhost/api/nested-throwable")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody()
                .jsonPath("$.type").doesNotExist()
                .jsonPath("$.title").isEqualTo("Internal Server Error")
                .jsonPath("$.status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.detail").isEqualTo("Illegal State")
                .jsonPath("$.stacktrace").isArray()
                .jsonPath(containsStringExpression("stacktrace[0]", "newIllegalState")).hasJsonPath()
                .jsonPath(containsStringExpression("stacktrace[1]", "nestedThrowable")).hasJsonPath()
                .jsonPath("$.cause.type").doesNotExist()
                .jsonPath("$.cause.title").isEqualTo("Internal Server Error")
                .jsonPath("$.cause.status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.cause.detail").isEqualTo("Illegal Argument")
                .jsonPath("$.cause.stacktrace").isArray()
                .jsonPath(containsStringExpression("cause.stacktrace[0]", "newIllegalArgument")).hasJsonPath()
                .jsonPath(containsStringExpression("cause.stacktrace[1]", "nestedThrowable")).hasJsonPath()
                .jsonPath("$.cause.cause.type").doesNotExist()
                .jsonPath("$.cause.cause.title").isEqualTo("Internal Server Error")
                .jsonPath("$.cause.cause.status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.cause.cause.detail").isEqualTo("No such element")
                .jsonPath("$.cause.cause.stacktrace").isArray()
                .jsonPath(containsStringExpression("cause.cause.stacktrace[0]", "newNoSuchElement")).hasJsonPath()
                .jsonPath(containsStringExpression("cause.cause.stacktrace[1]", "nestedThrowable")).hasJsonPath()
                .jsonPath("$.cause.cause.cause").doesNotExist();
    }

    @Test
    void nonAnnotatedNestedThrowable() {
        webTestClient().get().uri("http://localhost/api/handler-throwable-annotated")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_IMPLEMENTED)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody()
                .jsonPath("$.type").doesNotExist()
                .jsonPath("$.title").isEqualTo("Not Implemented")
                .jsonPath("$.status").isEqualTo(HttpStatus.NOT_IMPLEMENTED.value())
                .jsonPath("$.cause.type").doesNotExist()
                .jsonPath("$.cause.title").isEqualTo("Internal Server Error")
                .jsonPath("$.cause.status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.cause.cause.type").doesNotExist()
                .jsonPath("$.cause.cause.title").isEqualTo("Internal Server Error")
                .jsonPath("$.cause.cause.status").isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .jsonPath("$.cause.cause.cause").doesNotExist();
    }

    @Test
    void annotatedNestedThrowable() {
        webTestClient().get().uri("http://localhost/api/handler-throwable-annotated-cause")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_IMPLEMENTED)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody()
                .jsonPath("$.type").doesNotExist()
                .jsonPath("$.title").isEqualTo("Not Implemented")
                .jsonPath("$.status").isEqualTo(HttpStatus.NOT_IMPLEMENTED.value())
                .jsonPath("$.detail").isEqualTo("expected")
                .jsonPath("$.cause.type").doesNotExist()
                .jsonPath("$.cause.title").isEqualTo("Not Implemented")
                .jsonPath("$.cause.status").isEqualTo(HttpStatus.NOT_IMPLEMENTED.value())
                .jsonPath("$.cause.cause.type").doesNotExist()
                .jsonPath("$.cause.cause.title").isEqualTo("Not Implemented")
                .jsonPath("$.cause.cause.status").isEqualTo(HttpStatus.NOT_IMPLEMENTED.value())
                .jsonPath("$.cause.cause.cause").doesNotExist();
    }

    private static String containsStringExpression(String path, String substring) {
        return String.format("$[?(@.%s =~ /.*%s.*/)].%s", path, substring, path);
    }

}
