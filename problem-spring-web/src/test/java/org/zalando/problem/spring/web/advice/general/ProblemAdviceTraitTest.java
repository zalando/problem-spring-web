package org.zalando.problem.spring.web.advice.general;

import org.junit.jupiter.api.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class ProblemAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void throwableProblem() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/handler-problem"))
                .andExpect(status().isConflict())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Expected")))
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.detail", is("Nothing out of the ordinary")));
    }

}
