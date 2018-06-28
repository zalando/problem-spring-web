package org.zalando.problem.spring.webflux.advice.http;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.MethodNotAllowedException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;

final class MethodNotAllowedAdviceTraitTest implements AdviceTraitTesting {

    @Test
    public void methodNotAllowed() {
        Problem problem = webTestClient().post().uri("http://localhost/api/handler-problem")
                .accept(MediaType.valueOf("application/x.bla+json"), MediaTypes.PROBLEM)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectHeader().valueEquals("Allow", "GET")
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Method Not Allowed"));
        assertThat(problem.getStatus(), is(Status.METHOD_NOT_ALLOWED));
        assertThat(problem.getDetail(), containsString("not supported"));
    }

    @Test
    void noAllowIfNullAllowed() {
        final MethodNotAllowedAdviceTrait unit = new MethodNotAllowedAdviceTrait() {
        };

        final ResponseEntity<Problem> entity = unit.handleRequestMethodNotSupportedException(
                new MethodNotAllowedException("non allowed", null),
                MockServerWebExchange.from(MockServerHttpRequest.get("/").build())).block();

        assertThat(entity.getHeaders(), not(hasKey("Allow")));
    }

    @Test
    void noAllowIfNoneAllowed() {
        final MethodNotAllowedAdviceTrait unit = new MethodNotAllowedAdviceTrait() {
        };
        final ResponseEntity<Problem> entity = unit.handleRequestMethodNotSupportedException(
                new MethodNotAllowedException("non allowed", Collections.emptyList()),
                MockServerWebExchange.from(MockServerHttpRequest.get("/").build())).block();

        assertThat(entity.getHeaders(), not(hasKey("Allow")));
    }

}
