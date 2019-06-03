package org.zalando.problem.spring.web.advice.network;

import org.junit.jupiter.api.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class SocketTimeoutAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void socketTimeout() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/socket-timeout"))
                .andExpect(status().isGatewayTimeout())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Gateway Timeout")))
                .andExpect(jsonPath("$.status", is(504)))
                .andExpect(jsonPath("$.detail").doesNotExist());
    }

}
