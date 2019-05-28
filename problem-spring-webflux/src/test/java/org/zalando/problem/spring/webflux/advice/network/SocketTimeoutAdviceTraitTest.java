package org.zalando.problem.spring.webflux.advice.network;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import javax.annotation.Nullable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SocketTimeoutAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void socketTimeout() {
        @Nullable final Problem problem = webTestClient().get().uri("http://localhost/api/socket-timeout")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.GATEWAY_TIMEOUT)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertNotNull(problem);
        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Gateway Timeout"));
        assertThat(problem.getStatus(), is(Status.GATEWAY_TIMEOUT));
    }

}
