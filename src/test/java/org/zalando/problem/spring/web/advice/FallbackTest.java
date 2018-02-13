package org.zalando.problem.spring.web.advice;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;

import java.util.Optional;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

final class FallbackTest implements AdviceTraitTesting {

    @Override
    public ProblemHandling unit() {
        return new FallbackProblemHandling();
    }

    @Test
    void customFallbackUsed() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/handler-problem")
                .accept("text/xml"))
                .andExpect(status().isConflict())
                .andExpect(content().string(not(emptyString())))
                .andExpect(header().string("Content-Type", "text/xml"))
                .andExpect(header().string("X-Fallback-Used", is("true")));
    }

    @ControllerAdvice
    private static class FallbackProblemHandling implements ProblemHandling {

        @Override
        public Optional<MediaType> negotiate(final NativeWebRequest request) {
            return Optional.empty();
        }

        @Override
        public ResponseEntity<Problem> fallback(final Throwable throwable, final Problem problem,
                final NativeWebRequest request, final HttpHeaders headers) {
            return ResponseEntity
                    .status(problem.getStatus().getStatusCode())
                    .contentType(MediaType.TEXT_XML)
                    .header("X-Fallback-Used", Boolean.toString(true))
                    .body(problem);
        }

    }

}
