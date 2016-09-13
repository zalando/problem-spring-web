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
    public void invalidFormat() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/json-decimal")
                .contentType("application/json")
                .content("\"foobar\""))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("Can not construct instance of java.math.BigDecimal from String value 'foobar': not a valid representation")));
    }

    @Test
    public void noConstructor() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/json-user")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("Could not read document")))
                .andExpect(jsonPath("$.detail", containsString("No suitable constructor found for type [simple type, class org.zalando.problem.spring.web.advice.example.User]")))
                .andExpect(jsonPath("$.detail", containsString("can not instantiate from JSON object")))
                .andExpect(jsonPath("$.detail", containsString("missing default constructor or creator, or perhaps need to add/enable type information?")));
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