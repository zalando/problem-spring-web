package org.zalando.problem.spring.webflux.advice.validation;

import org.junit.jupiter.api.Test;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;
import org.zalando.problem.violations.ConstraintViolationProblem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

final class BindAdviceTraitTest implements AdviceTraitTesting {


    //TODO: Move this. Exception thrown is not a WebExchangeBindException but a ServerWebInputException
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

}
