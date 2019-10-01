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
import static org.hamcrest.Matchers.notNullValue;

final class ConstraintViolationAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void invalidRequestParam() {
        final ConstraintViolationProblem problem = webTestClient().post().uri("http://localhost/api/handler-invalid-param")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Bob\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(ConstraintViolationProblem.class).returnResult().getResponseBody();

        assertThat(problem, is(notNullValue()));
        assertThat(problem.getType().toString(), is("https://zalando.github.io/problem/constraint-violation"));
        assertThat(problem.getTitle(), is("Constraint Violation"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getViolations(), hasSize(1));
        assertThat(problem.getViolations().get(0).getField(), is(""));
        assertThat(problem.getViolations().get(0).getMessage(), is("must not be called Bob"));
    }

}
