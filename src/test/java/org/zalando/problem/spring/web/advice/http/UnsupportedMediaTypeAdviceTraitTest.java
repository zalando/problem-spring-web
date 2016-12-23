package org.zalando.problem.spring.web.advice.http;

import org.junit.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class UnsupportedMediaTypeAdviceTraitTest implements AdviceTraitTesting {

    @Test
    public void unsupportedMediaType() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/handler-put")
                .contentType("application/atom+xml"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(header().string("Accept", containsString("application/json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Unsupported Media Type")))
                .andExpect(jsonPath("$.status", is(415)))
                .andExpect(jsonPath("$.detail", containsString("application/atom+xml")));
    }

    @Test
    public void acceptHeaderIfSupported() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/handler-put")
                .contentType("application/atom+xml"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(header().string("Accept", containsString("application/json")))
                .andExpect(header().string("Accept", containsString("application/xml")));
    }

}
