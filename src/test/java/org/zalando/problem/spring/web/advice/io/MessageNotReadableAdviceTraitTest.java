package org.zalando.problem.spring.web.advice.io;

import org.junit.jupiter.api.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class MessageNotReadableAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void missingRequestBody() throws Exception {
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
    void malformedJsonRequestBody() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/json-object")
                .contentType("application/json")
                .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("Unexpected end-of-input")));
    }

    @Test
    void invalidFormat() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/json-decimal")
                .contentType("application/json")
                .content("\"foobar\""))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("java.math.BigDecimal")))
                .andExpect(jsonPath("$.detail", containsString("foobar")));
    }

    @Test
    void noConstructor() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/json-user")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("org.zalando.problem.spring.web.advice.example.User")));
    }

    @Test
    void wrongJsonTypeRequestBody() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/json-object")
                .contentType("application/json")
                .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("java.util.LinkedHashMap")))
                .andExpect(jsonPath("$.detail", containsString("START_ARRAY")));
    }

}
