package org.zalando.problem.spring.webflux.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.http.HttpAdviceTrait;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

class ProblemExceptionHandlerTest {

    private ProblemExceptionHandler handler = new ProblemExceptionHandler(new ObjectMapper(), new HttpAdviceTrait() {});

    private MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());

    @Test
    void shouldHandleResponseStatusException() {
        MockServerHttpResponse response = exchange.getResponse();
        handler.handle(exchange, new ResponseStatusException(HttpStatus.CONFLICT)).block();

        assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));
        assertThat(response.getHeaders().getContentType(), is(MediaTypes.PROBLEM));
        assertThat(response.getBodyAsString().block(), containsString(Status.CONFLICT.getReasonPhrase()));
    }

    @Test
    void shouldPropagateOtherExceptions() {
        try {
            handler.handle(exchange, new RuntimeException("mock exception")).block();
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("mock exception"));
        }
    }
}