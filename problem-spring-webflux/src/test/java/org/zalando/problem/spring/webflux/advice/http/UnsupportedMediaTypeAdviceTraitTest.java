package org.zalando.problem.spring.webflux.advice.http;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

final class UnsupportedMediaTypeAdviceTraitTest implements AdviceTraitTesting {

    @Test
    void unsupportedMediaType() {
        Problem problem = webTestClient().put().uri("http://localhost/api/handler-put")
                .contentType(MediaType.APPLICATION_ATOM_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectHeader().valueEquals("Accept", "application/json, application/xml")
                .expectBody(Problem.class).returnResult().getResponseBody();

        assertThat(problem.getType().toString(), is("about:blank"));
        assertThat(problem.getTitle(), is("Unsupported Media Type"));
        assertThat(problem.getStatus(), is(Status.UNSUPPORTED_MEDIA_TYPE));
        assertThat(problem.getDetail(), containsString(MediaType.APPLICATION_ATOM_XML_VALUE));
    }

}
