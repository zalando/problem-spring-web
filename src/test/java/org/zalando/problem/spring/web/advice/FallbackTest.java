package org.zalando.problem.spring.web.advice;


import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class FallbackTest implements AdviceTraitTesting {

    @Override
    public Object unit() {
        return new FallbackProblemHandling();
    }

    @Test
    public void customFallbackUsed() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/handler-problem")
                .accept("text/html"))
                .andExpect(status().isConflict())
                .andExpect(content().string(""))
                .andExpect(header().doesNotExist("Content-Type"))
                .andExpect(header().string("X-Fallback-Used", is("true")));
    }

    @ControllerAdvice
    private static class FallbackProblemHandling implements ProblemHandling {

        @Override
        public ResponseEntity<Problem> fallback(final Throwable throwable, final Problem problem,
                                                final NativeWebRequest request,
                                                final HttpHeaders headers) {
            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .header("X-Fallback-Used", Boolean.toString(true))
                    .body(null);
        }

    }

}
