package org.zalando.problem.spring.webflux.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;
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
    @Test
    void shouldHandleMethodNotFoundException() {
        MockServerHttpResponse response = exchange.getResponse();
        handler.handle(exchange, new MethodNotAllowedException(HttpMethod.GET, List.of(HttpMethod.POST))).block();
        assertThat(response.getStatusCode(), is(HttpStatus.METHOD_NOT_ALLOWED));
    }
    @Test
    void shouldHandleUnsupportedMediaTypeStatusException() {
        MockServerHttpResponse response = exchange.getResponse();
        handler.handle(exchange, new UnsupportedMediaTypeStatusException("sample")).block();
        assertThat(response.getStatusCode(), is(HttpStatus.UNSUPPORTED_MEDIA_TYPE));
    }
    @Test
    void shouldHandleNotAcceptableStatusException() {
        MockServerHttpResponse response = exchange.getResponse();
        handler.handle(exchange, new NotAcceptableStatusException("sample")).block();
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_ACCEPTABLE));
    }
}