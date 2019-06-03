package org.zalando.problem.spring.webflux.advice.network;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;

import javax.annotation.Nullable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CircuitBreakerOpenAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void circuitBreakerOpen() {
        @Nullable final Problem problem = webTestClient().post().uri("http://localhost/api/handler-circuit-breaker-open")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertNotNull(problem);
        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Service Unavailable"));
        assertThat(problem.getStatus(), is(Status.SERVICE_UNAVAILABLE));
    }

    @Override
    public ProblemHandling unit() {
        return new CustomExceptionHandling();
    }

    @ControllerAdvice
    private static final class CustomExceptionHandling implements ProblemHandling, CircuitBreakerOpenAdviceTrait {

    }

}
