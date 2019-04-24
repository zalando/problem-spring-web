package org.zalando.problem.spring.web.advice.network;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class CircuitBreakerOpenAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void typeMismatch() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/handler-circuit-breaker-open"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Service Unavailable")))
                .andExpect(jsonPath("$.status", is(503)))
                .andExpect(jsonPath("$.detail").doesNotExist());
    }

    @Override
    public ProblemHandling unit() {
        return new CustomExceptionHandling();
    }

    @ControllerAdvice
    private static final class CustomExceptionHandling implements ProblemHandling, CircuitBreakerOpenAdviceTrait {

    }

}
