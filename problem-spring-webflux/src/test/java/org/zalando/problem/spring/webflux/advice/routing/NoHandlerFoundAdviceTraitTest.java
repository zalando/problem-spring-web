package org.zalando.problem.spring.webflux.advice.routing;

import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

final class NoHandlerFoundAdviceTraitTest implements AdviceTraitTesting {

    // TODO: Move as it's set by the custom exceptionhandler
    @Test
    void noHandlerInController() {
        Problem problem = webTestClient().get().uri("http://localhost/api/no-handler")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Not Found"));
        assertThat(problem.getStatus(), is(Status.NOT_FOUND));
        assertThat(problem.getDetail(), containsString("No matching handler"));
    }

    @Test
    void noHandler() {
        Problem problem = webTestClient().get().uri("http://localhost/no-handler")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Not Found"));
        assertThat(problem.getStatus(), is(Status.NOT_FOUND));
        assertThat(problem.getDetail(), containsString("No matching handler"));
    }


}
