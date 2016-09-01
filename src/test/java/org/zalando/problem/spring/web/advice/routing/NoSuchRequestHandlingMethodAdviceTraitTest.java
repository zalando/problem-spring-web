package org.zalando.problem.spring.web.advice.routing;


import org.junit.Test;
import org.springframework.http.MediaType;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class NoSuchRequestHandlingMethodAdviceTraitTest implements AdviceTraitTesting {

    @Test
    public void noSuchRequestHandlingMethod() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/noSuchRequestHandlingMethod")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail", containsString("No matching handler method found")));
    }

}