package org.zalando.problem.spring.webflux.advice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class AdviceUtilsTest {

    private ResponseEntity<Problem> responseEntity;

    private MockServerWebExchange webExchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void init() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaTypes.PROBLEM);

        responseEntity = ResponseEntity
                .status(HttpStatus.CONFLICT)
                .headers(headers)
                .body(Problem.valueOf(Status.CONFLICT));
    }

    @Test
    void shouldSetHttpResponse() {
        MockServerHttpResponse response = webExchange.getResponse();

        AdviceUtils.setHttpResponse(responseEntity, webExchange, mapper)
                .map(it -> response.setComplete())
                .block();

        assertThat(response.getStatusCode(), is(HttpStatus.CONFLICT));
        assertThat(response.getHeaders().getContentType(), is(MediaTypes.PROBLEM));
        assertThat(response.getBodyAsString().block(), containsString(Status.CONFLICT.getReasonPhrase()));

    }

    @Test
    void shouldPropagateJsonProcessingException() throws Exception {
        ObjectMapper mapper = mock(ObjectMapper.class);
        when(mapper.writeValueAsBytes(any())).thenThrow(mock(JsonProcessingException.class));

        try {
            AdviceUtils.setHttpResponse(responseEntity, webExchange, mapper).block();
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat((JsonProcessingException)e.getCause(), isA(JsonProcessingException.class));
        }
    }



    @Test
    void shouldPropagateWriteHandlerError() {
        webExchange.getResponse().setWriteHandler(body -> Mono.error(new RuntimeException("mock exception")));
        try {
            AdviceUtils.setHttpResponse(responseEntity, webExchange, mapper).block();
            fail("A RuntimeException should have been thrown");
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), is("mock exception"));
        }
    }
}