package org.zalando.problem.spring.webflux.advice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;
import org.zalando.problem.violations.ConstraintViolationProblem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

final class MethodArgumentNotValidAdviceTraitTest implements AdviceTraitTesting {

    // TODO : Move as Exception thrown is not a MethodArgumentNotValidException but a WebExchangeBindException
    @Test
    void invalidRequestBodyField() {
        ConstraintViolationProblem problem = webTestClient().post().uri("http://localhost/api/handler-invalid-body")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("{\"name\":\"Jo\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(ConstraintViolationProblem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("https://zalando.github.io/problem/constraint-violation"));
        assertThat(problem.getTitle(), is("Constraint Violation"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getViolations().size(), is(1));
        assertThat(problem.getViolations().get(0).getField(), is("name"));
        assertThat(problem.getViolations().get(0).getMessage(), startsWith("size must be between 3 and 10"));
    }

    // TODO : Move as Exception thrown is not a MethodArgumentNotValidException but a WebExchangeBindException
    @Test
    void invalidRequestBody() {
        ConstraintViolationProblem problem = webTestClient().post().uri("http://localhost/api/handler-invalid-body")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("{\"name\":\"Bob\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(ConstraintViolationProblem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("https://zalando.github.io/problem/constraint-violation"));
        assertThat(problem.getTitle(), is("Constraint Violation"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getViolations().size(), is(1));
        assertThat(problem.getViolations().get(0).getField(), is("user_request"));
        assertThat(problem.getViolations().get(0).getMessage(), is("must not be called Bob"));
    }

}
