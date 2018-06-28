package org.zalando.problem.spring.webflux.advice.http;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

final class NotAcceptableAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void notAcceptable() {
        Problem problem = webTestClient().get().uri("http://localhost/api/handler-ok")
                .accept(MediaType.valueOf("application/x.vnd.specific+json"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Not Acceptable"));
        assertThat(problem.getStatus(), is(Status.NOT_ACCEPTABLE));
        assertThat(problem.getDetail(), containsString("Could not find acceptable representation"));
    }

    @Test
    void notAcceptableNoProblem() {
        Problem problem = webTestClient().get().uri("http://localhost/api/handler-ok")
                .accept(MediaType.IMAGE_PNG)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Not Acceptable"));
        assertThat(problem.getStatus(), is(Status.NOT_ACCEPTABLE));
        assertThat(problem.getDetail(), containsString("Could not find acceptable representation"));
    }

}
