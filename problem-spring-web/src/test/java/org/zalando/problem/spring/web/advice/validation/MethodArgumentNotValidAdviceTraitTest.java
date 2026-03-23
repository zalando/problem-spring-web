package org.zalando.problem.spring.web.advice.validation;

import org.junit.jupiter.api.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class MethodArgumentNotValidAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void invalidRequestBodyField() throws Exception {
        mvc().perform(request(POST, "http://localhost/api/handler-invalid-body")
                .contentType("application/json")
                .content("{\"name\":\"Jo\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("name")))
                .andExpect(jsonPath("$.violations[0].message", startsWith("size must be between 3 and 10")));
    }

    @Test
    void invalidRequestBody() throws Exception {
        mvc().perform(request(POST, "http://localhost/api/handler-invalid-body")
                .contentType("application/json")
                .content("{\"name\":\"Bob\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("https://zalando.github.io/problem/constraint-violation")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("userRequest")))
                .andExpect(jsonPath("$.violations[0].message", is("must not be called Bob")));
    }

}
