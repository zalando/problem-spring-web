package org.zalando.problem.spring.webflux.advice.http;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

final class ResponseStatusAdviceTraitTest implements AdviceTraitTesting {

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
        assertThat(problem.getDetail(), containsString("No request body"));
    }

    @Test
    void malformedJsonRequestBody() {
        Problem problem = webTestClient().put().uri("http://localhost/api/json-object")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("Failed to read HTTP message"));
    }

    @Test
    void invalidFormat() {
        Problem problem = webTestClient().put().uri("http://localhost/api/json-decimal")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("\"foobar\"")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("Failed to read HTTP message"));
    }

    @Test
    void noConstructor() {
        Problem problem = webTestClient().put().uri("http://localhost/api/json-user")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("Failed to read HTTP message"));
    }

    @Test
    void wrongJsonTypeRequestBody() {
        Problem problem = webTestClient().put().uri("http://localhost/api/json-object")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("[]")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("Failed to read HTTP message"));

    }

    @Test
    void multipart() {
        Problem problem = webTestClient().post().uri("http://localhost/api/handler-multipart")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("Required request part"));
        assertThat(problem.getDetail(), containsString("is not present"));
    }

    @Test
    void multipartMissingPart() {
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
        assertThat(problem.getDetail(), containsString("Type mismatch"));
    }

    @Test
    void missingServletRequestParameter() {
        Problem problem = webTestClient().get().uri("http://localhost/api/handler-params")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getDetail(), containsString("params1"));
    }

    @Test
    void servletRequestBinding() {
        Problem problem = webTestClient().get().uri("http://localhost/api/handler-headers")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Bad Request"));
        assertThat(problem.getStatus(), is(Status.BAD_REQUEST));
        assertThat(problem.getDetail(), containsString("X-Custom-Header"));

    }

    @Test
    void noHandlerInController() {
        Problem problem = webTestClient().get().uri("http://localhost/api/no-handler")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Not Found"));
        assertThat(problem.getStatus(), is(Status.NOT_FOUND));
        assertThat(problem.getDetail(), containsString("404 NOT_FOUND"));
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
        assertThat(problem.getDetail(), containsString("404 NOT_FOUND"));
    }

}
