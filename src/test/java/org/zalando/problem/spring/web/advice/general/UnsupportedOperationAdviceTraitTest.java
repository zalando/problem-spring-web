package org.zalando.problem.spring.web.advice.general;

import org.junit.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class UnsupportedOperationAdviceTraitTest implements AdviceTraitTesting {

    @Test
    public void unsupportedOperation() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/not-implemented"))
                .andExpect(status().isNotImplemented())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("https://httpstatuses.com/501")))
                .andExpect(jsonPath("$.title", is("Not Implemented")))
                .andExpect(jsonPath("$.status", is(501)))
                .andExpect(jsonPath("$.detail", is("Not yet implemented")));
    }

}
