package org.zalando.problem.spring.web.advice.general;


import org.junit.jupiter.api.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class ThrowableAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void throwable() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/handler-throwable"))
                .andExpect(status().isInternalServerError())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Internal Server Error")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.detail", containsString("expected")))
                .andExpect(jsonPath("$.stacktrace").doesNotExist())
                .andExpect(jsonPath("$.cause").doesNotExist());
    }

}
