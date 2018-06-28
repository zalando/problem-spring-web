package org.zalando.problem.spring.webflux.advice.general;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class ProblemAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void throwableProblem() {
        Problem problem = webTestClient().get().uri("http://localhost/api/handler-problem")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Expected"));
        assertThat(problem.getStatus(), is(Status.CONFLICT));
        assertThat(problem.getDetail(), is("Nothing out of the ordinary"));
    }

}
