package org.zalando.problem.spring.webflux.advice.routing;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.BodyInserters;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

final class MissingServletRequestPartAdviceTraitTest implements AdviceTraitTesting {

    //TODO : move this as it's a ServerWebInputException (ResponseStatusException)
    @Test
    void multipart() {
        Problem problem = webTestClient().post().uri("http://localhost/api/handler-multipart")
                .body(BodyInserters.fromMultipartData("payload1", new byte[]{0x1}))
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("payload2"));

    }

}
