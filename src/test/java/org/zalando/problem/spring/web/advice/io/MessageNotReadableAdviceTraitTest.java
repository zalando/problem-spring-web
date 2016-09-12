package org.zalando.problem.spring.web.advice.io;


import org.junit.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class MessageNotReadableAdviceTraitTest implements AdviceTraitTesting {

    @Test
    public void missingRequestBody() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/handler-put")
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("request body is missing")));
    }

    @Test
    public void malformedJsonRequestBody() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/json-object")
                .contentType("application/json")
                .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("Unexpected end-of-input: expected close marker for OBJECT")))
                .andExpect(jsonPath("$.detail", containsString("line: 1, column: 0")));
    }

    @Test
    public void wrongJsonTypeRequestBody() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/json-object")
                .contentType("application/json")
                .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("Can not deserialize instance of java.util.LinkedHashMap out of START_ARRAY token")))
                .andExpect(jsonPath("$.detail", containsString("line: 1, column: 1")));
    }

}