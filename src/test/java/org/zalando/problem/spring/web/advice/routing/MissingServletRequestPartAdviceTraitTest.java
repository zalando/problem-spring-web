package org.zalando.problem.spring.web.advice.routing;

import org.junit.jupiter.api.Test;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class MissingServletRequestPartAdviceTraitTest implements AdviceTraitTesting {

    @Test
    @SuppressWarnings("deprecation") // TODO use multipart(String) when Spring 4 support is no longer needed
    void multipart() throws Exception {
        mvc().perform(fileUpload("http://localhost/api/handler-multipart")
                .file("payload1", new byte[]{0x1}))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", containsString("payload2")));
    }

}
