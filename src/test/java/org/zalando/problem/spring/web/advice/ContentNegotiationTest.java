package org.zalando.problem.spring.web.advice;


import com.google.gag.annotation.remark.Hack;
import org.junit.Ignore;
import org.junit.Test;

import javax.servlet.ServletException;

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
                .accept("application/x.something+json", "application/x.problem+json"))
                .andExpect(content().contentType(MediaTypes.X_PROBLEM));
    }

    @Test
    public void specificityWins() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/*", "application/x.problem+json"))
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
    @Hack("This is actually rather shady, but it's most likely what the client actually wants")
    public void specificJsonGivesProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/x.vendor.specific+json"))
                .andExpect(content().contentType(MediaTypes.PROBLEM));
    }

    @Test
    public void nonJsonIsNotAcceptable() throws Exception {
        mvc().perform(request(GET, url)
                .header("Accept", "application/atom+xml"))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string(""))
                .andExpect(header().doesNotExist("Content-Type"));
    }

    @Test(expected = ServletException.class)
    public void invalidMediaTypeIsNotAcceptable() throws Exception {
        mvc().perform(request(GET, url)
                .header("Accept", "application/"));

    }

}
