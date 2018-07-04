package org.zalando.problem.spring.webflux.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.compose;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.RESET_CONTENT;
import static org.zalando.problem.spring.common.MediaTypes.PROBLEM;

final class SpringAdviceTraitTest {

    private final SpringAdviceTrait unit = new SpringAdviceTrait() {
    };

    @Test
    void buildsOnThrowable() {
        final HttpStatusAdapter adapter = new HttpStatusAdapter(RESET_CONTENT);

        final MockServerWebExchange request = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        final ResponseEntity<Problem> result = unit.create(RESET_CONTENT,
                new IllegalStateException("Message"), request).block();

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(adapter)))
                .and(hasFeature("Detail", Problem::getDetail, is("Message"))));
    }

    @Test
    void toProblemWithoutCause() {
        final ThrowableProblem problem = unit.toProblem(new IllegalStateException("Message"), BAD_REQUEST);

        assertThat(problem.getCause(), nullValue());
        assertThat(problem.getMessage(),
                allOf(containsString(BAD_REQUEST.getReasonPhrase()), containsString("Message")));
    }
}
