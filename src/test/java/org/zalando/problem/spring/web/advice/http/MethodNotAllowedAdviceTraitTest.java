package org.zalando.problem.spring.web.advice.http;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class MethodNotAllowedAdviceTraitTest implements AdviceTraitTesting {

    @Test
    public void methodNotAllowed() throws Exception {
        mvc().perform(request(POST, "http://localhost/api/handler-problem")
                .accept("application/x.bla+json", "application/problem+json"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(header().string("Allow", is("GET")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Method Not Allowed")))
                .andExpect(jsonPath("$.status", is(405)))
                .andExpect(jsonPath("$.detail", containsString("not supported")));
    }

    @Test
    void noAllowIfNullAllowed() {
        final MethodNotAllowedAdviceTrait unit = new MethodNotAllowedAdviceTrait() {
        };
        final ResponseEntity<Problem> entity = unit.handleRequestMethodNotSupportedException(
                new HttpRequestMethodNotSupportedException("non allowed"), mock(NativeWebRequest.class));

        assertThat(entity.getHeaders(), not(hasKey("Allow")));
    }

    @Test
    void noAllowIfNoneAllowed() {
        final MethodNotAllowedAdviceTrait unit = new MethodNotAllowedAdviceTrait() {
        };
        final ResponseEntity<Problem> entity = unit.handleRequestMethodNotSupportedException(
                new HttpRequestMethodNotSupportedException("non allowed", new String[]{}), mock(NativeWebRequest.class));

        assertThat(entity.getHeaders(), not(hasKey("Allow")));
    }

}
