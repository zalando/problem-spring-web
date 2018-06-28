package org.zalando.problem.spring.webflux.advice.general;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class UnsupportedOperationAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void unsupportedOperation() {
        Problem problem = webTestClient().get().uri("http://localhost/api/not-implemented")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_IMPLEMENTED)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Not Implemented"));
        assertThat(problem.getStatus(), is(Status.NOT_IMPLEMENTED));
        assertThat(problem.getDetail(), is("Not yet implemented"));
    }

}
