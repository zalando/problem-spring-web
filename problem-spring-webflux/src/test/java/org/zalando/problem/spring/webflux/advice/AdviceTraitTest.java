package org.zalando.problem.spring.webflux.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.compose;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.RESET_CONTENT;
import static org.zalando.problem.spring.common.MediaTypes.PROBLEM;

public class AdviceTraitTest {

    private final AdviceTrait unit = new AdviceTrait() {
    };

    @Test
    void buildsOnProblem() {
        final ThrowableProblem problem = mock(ThrowableProblem.class);
        when(problem.getStatus()).thenReturn(Status.RESET_CONTENT);

        final ResponseEntity<Problem> result = unit.create(problem, request()).block();

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)));
    }

    @Test
    void buildsOnThrowable() {
        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT,
                new IllegalStateException("Message"), request()).block();

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is("Message"))));
    }

    @Test
    void buildsOnThrowableWithType() {
        final URI type = URI.create("https://google.com");
        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT,
          new IllegalStateException("Message"), request(), type).block();

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
          .and(hasFeature("Detail", Problem::getDetail, is("Message"))).and(hasFeature("Type", Problem::getType, is(type))));
    }

    @Test
    void buildsIfIncludes() {
        final String message = "Message";

        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT,
                new IllegalStateException(message),
                request("application/*+json")).block();

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is(message))));
    }

    @Test
    void mapsStatus() {
        final HttpStatus expected = HttpStatus.BAD_REQUEST;
        final StatusType input = Status.BAD_REQUEST;
        final ResponseEntity<Problem> entity = unit.create(input,
                new IllegalStateException("Checkpoint"), request()).block();

        assertThat(entity.getStatusCode(), is(expected));
    }

    @Test
    void throwsOnUnknownStatus() {
        final StatusType input = mock(StatusType.class);
        when(input.getReasonPhrase()).thenReturn("L33t");
        when(input.getStatusCode()).thenReturn(1337);

        assertThrows(IllegalArgumentException.class, () ->
                unit.create(input, new IllegalStateException("L33t"), request()).block());
    }

    private ServerWebExchange request(final String acceptMediaType) {
        return MockServerWebExchange.from(
                MockServerHttpRequest.get("/")
                        .accept(MediaType.valueOf(acceptMediaType))
        );
    }

    private ServerWebExchange request() {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/"));
    }

}
