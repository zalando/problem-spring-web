package org.zalando.problem.spring.webflux.advice;

import com.google.gag.annotation.remark.Hack;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.MediaTypes;

public final class ContentNegotiationTest implements AdviceTraitTesting {

    private final String url = "http://localhost/api/handler-problem";

    @Test
    public void problemGivesProblem() {
        webTestClient().get().uri(url)
                .accept(MediaTypes.PROBLEM)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class);
    }

    @Test
    public void xproblemGivesXProblem() {
        webTestClient().get().uri(url)
                .accept(MediaType.valueOf("application/x.something+json"), MediaType.valueOf("application/x.problem+json"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaTypes.X_PROBLEM)
                .expectBody(Problem.class);
    }

    @Test
    public void specificityWins() {
        webTestClient().get().uri(url)
                .accept(MediaType.valueOf("application/*"), MediaType.valueOf("application/x.problem+json"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaTypes.X_PROBLEM)
                .expectBody(Problem.class);
    }

    @Test
    public void jsonGivesProblem() {
        webTestClient().get().uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class);
    }

    @Test
    public void wildcardJsonGivesProblem() {
        webTestClient().get().uri(url)
                .accept(MediaType.valueOf("application/*+json"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class);
    }

    @Test
    @Hack("This is actually rather shady, but it's most likely what the client actually wants")
    public void specificJsonGivesProblem() {
        webTestClient().get().uri(url)
                .accept(MediaType.valueOf("application/x.vendor.specific+json"))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class);
    }

    @Test
    public void nonJsonFallsBackToProblemJson() {
        webTestClient().get().uri(url)
                .accept(MediaType.APPLICATION_ATOM_XML)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody(Problem.class);
    }

    @Test
    public void invalidMediaTypeIsNotAcceptable() {
        webTestClient().get().uri(url)
                .header("Accept", "application/")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE);

    }

}
