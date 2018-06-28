package org.zalando.problem.spring.webflux.advice.io;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

final class MessageNotReadableAdviceTraitTest implements AdviceTraitTesting {

    // TODO: Move all this
    @Test
    void missingRequestBody() {
        Problem problem = webTestClient().put().uri("http://localhost/api/handler-put")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("Request body is missing"));
    }

    @Test
    void malformedJsonRequestBody() {
        Problem problem = webTestClient().put().uri("http://localhost/api/json-object")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("{")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("Unexpected end-of-input"));
    }

    @Test
    void invalidFormat() {
        Problem problem = webTestClient().put().uri("http://localhost/api/json-decimal")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("\"foobar\"")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("java.math.BigDecimal"));
        assertThat(problem.getDetail(), containsString("foobar"));
    }

    @Test
    void noConstructor() {
        Problem problem = webTestClient().put().uri("http://localhost/api/json-user")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("{}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("org.zalando.problem.spring.webflux.advice.example.User"));
    }

    @Test
    void wrongJsonTypeRequestBody() {
        Problem problem = webTestClient().put().uri("http://localhost/api/json-object")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody("[]")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("java.util.LinkedHashMap"));
        assertThat(problem.getDetail(), containsString("START_ARRAY"));

    }

}
