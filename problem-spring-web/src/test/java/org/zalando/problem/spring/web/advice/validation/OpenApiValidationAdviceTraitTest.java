package org.zalando.problem.spring.web.advice.validation;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OpenApiValidationAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void invalidRequest() throws Exception {
        mvc().perform(request(POST, "http://localhost/api/openapi/invalid-request")
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("foo")))
                .andExpect(jsonPath("$.violations[0].message", is("not null")));
    }

    @Test
    void invalidResponse() throws Exception {
        mvc().perform(request(POST, "http://localhost/api/openapi/invalid-response")
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("foo")))
                .andExpect(jsonPath("$.violations[0].message", is("not null")));
    }

    @ControllerAdvice
    public static class ExceptionHandling implements ProblemHandling, OpenApiValidationAdviceTrait {

    }

    @Override
    public ProblemHandling unit() {
        return new ExceptionHandling();
    }

}
