package org.zalando.problem.spring.web.advice;


import org.junit.Ignore;
import org.junit.Test;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class ContentNegotiationTest implements AdviceTraitTesting {

    private final String url = "http://localhost/api/handler-problem";

    @Test
    public void problemGivesProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/problem+json"))
                .andExpect(content().contentType(MediaTypes.PROBLEM));
    }

    @Test
    public void xproblemGivesXProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/x.problem+json"))
                .andExpect(content().contentType(MediaTypes.X_PROBLEM));
    }

    @Test
    public void jsonGivesProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/json"))
                .andExpect(content().contentType(MediaTypes.PROBLEM));
    }

    @Ignore("https://jira.spring.io/browse/SPR-10493") // TODO enable as soon as this works
    @Test
    public void wildcardJsonGivesProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/*+json"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaTypes.PROBLEM));
    }

    @Test
    public void specificJsonGivesProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/x.vendor.specific+json"))
                .andExpect(content().contentType(MediaTypes.PROBLEM));
    }

    @Test
    public void nonJsonGivesEmpty() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/atom+xml"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(""))
                .andExpect(header().doesNotExist("Content-Type"));
    }

}
