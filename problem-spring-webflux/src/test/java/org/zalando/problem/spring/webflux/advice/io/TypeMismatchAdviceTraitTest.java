package org.zalando.problem.spring.webflux.advice.io;

import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

final class TypeMismatchAdviceTraitTest implements AdviceTraitTesting {


    // TODO: Move this
    @Test
    void typeMismatch() {
        Problem problem = webTestClient().get().uri("http://localhost/api/handler-conversion?dateTime=abc")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("Failed to convert"));
    }

}
