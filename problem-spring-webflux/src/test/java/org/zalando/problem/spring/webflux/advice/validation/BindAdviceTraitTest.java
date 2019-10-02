package org.zalando.problem.spring.webflux.advice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;
import org.zalando.problem.violations.ConstraintViolationProblem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

final class BindAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void invalidRequestQueryParams() {
        ConstraintViolationProblem problem = webTestClient().get().uri("http://localhost/api/handler-invalid-query-strings?page=-1&size=0")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(ConstraintViolationProblem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("https://zalando.github.io/problem/constraint-violation"));
        assertThat(problem.getTitle(), is("Constraint Violation"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getViolations(), hasSize(2));
        assertThat(problem.getViolations().get(0).getField(), is("page"));
        assertThat(problem.getViolations().get(0).getMessage(), is("must be greater than or equal to 0"));
        assertThat(problem.getViolations().get(1).getField(), is("size"));
        assertThat(problem.getViolations().get(1).getMessage(), is("must be greater than or equal to 1"));
    }

    @Test
    void invalidRequestBodyField() {
        ConstraintViolationProblem problem = webTestClient().post().uri("http://localhost/api/handler-invalid-body")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Jo\"}")
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

    @Test
    void invalidRequestBody() {
        ConstraintViolationProblem problem = webTestClient().post().uri("http://localhost/api/handler-invalid-body")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Bob\"}")
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
