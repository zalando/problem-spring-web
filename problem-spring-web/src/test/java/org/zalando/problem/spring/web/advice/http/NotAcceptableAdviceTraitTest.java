package org.zalando.problem.spring.web.advice.http;

import org.junit.jupiter.api.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class NotAcceptableAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void notAcceptable() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/handler-ok")
                .accept("application/x.vnd.specific+json"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Not Acceptable")))
                .andExpect(jsonPath("$.status", is(406)))
                .andExpect(jsonPath("$.detail", containsString("No acceptable representation")));
    }

    @Test
    void notAcceptableNoProblem() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/handler-ok")
                .accept("image/png"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Not Acceptable")))
                .andExpect(jsonPath("$.status", is(406)))
                .andExpect(jsonPath("$.detail", containsString("No acceptable representation")));
    }

}
