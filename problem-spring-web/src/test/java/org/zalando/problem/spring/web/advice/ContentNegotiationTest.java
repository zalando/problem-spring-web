package org.zalando.problem.spring.web.advice;

import com.google.gag.annotation.remark.Hack;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.zalando.problem.spring.common.MediaTypes;

import jakarta.servlet.ServletException;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class ContentNegotiationTest implements AdviceTraitTesting {

    private final String url = "http://localhost/api/handler-problem";

    @Test
    public void problemGivesProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/problem+json"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    public void xproblemGivesXProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/x.something+json", "application/x.problem+json"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaTypes.X_PROBLEM))
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    public void specificityWins() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/*", "application/x.problem+json"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaTypes.X_PROBLEM))
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    public void jsonGivesProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/json"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(content().string(not(emptyString())));
    }

    @Disabled("https://jira.spring.io/browse/SPR-10493") // TODO enable as soon as this works
    @Test
    public void wildcardJsonGivesProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/*+json"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    @Hack("This is actually rather shady, but it's most likely what the client actually wants")
    public void specificJsonGivesProblem() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/x.vendor.specific+json"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    public void nonJsonFallsBackToProblemJson() throws Exception {
        mvc().perform(request(GET, url)
                .accept("application/atom+xml"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(content().string(not(emptyString())));
    }

    @Test
    public void invalidMediaTypeIsNotAcceptable() {
        assertThrows(ServletException.class, () ->
                mvc().perform(request(GET, url)
                        .header("Accept", "application/")));

    }

}
