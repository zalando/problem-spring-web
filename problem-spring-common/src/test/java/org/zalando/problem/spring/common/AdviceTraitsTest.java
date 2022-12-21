package org.zalando.problem.spring.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import uk.org.lidalia.slf4jext.Level;
import com.github.valfirst.slf4jtest.LoggingEvent;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class AdviceTraitsTest {

    private final TestLogger log = TestLoggerFactory.getTestLogger(AdviceTraits.class);

    @BeforeEach
    @AfterEach
    void reset() {
        TestLoggerFactory.clear();
    }

    @SuppressWarnings("unused")
    private static HttpStatus[] data() {
        return HttpStatus.values();
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldLog4xxAsWarn(final HttpStatus status) {
        assumeTrue(status.is4xxClientError());
        AdviceTraits.log(new RuntimeException("Test message"), status);

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        assertThat(event.getLevel(), is(Level.WARN));
        assertThat(event.getMessage(), is("{}: {}"));
        assertThat(event.getArguments(), contains(status.getReasonPhrase(), "Test message"));
        assertThat(event.getThrowable().orElse(null), is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldLog5xxAsError(final HttpStatus status) {
        assumeTrue(status.is5xxServerError());
        final IOException throwable = new IOException();
        AdviceTraits.log(throwable, status);

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        assertThat(event.getLevel(), is(Level.ERROR));
        assertThat(event.getMessage(), is(status.getReasonPhrase()));
        assertThat(event.getArguments(), emptyIterable());
        assertThat(event.getThrowable().orElse(null), is(throwable));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldNotLogNon4xx5xxErrors(final HttpStatus status) {
        assumeFalse(status.is5xxServerError() || status.is4xxClientError());
        final IOException throwable = new IOException();
        AdviceTraits.log(throwable, status);

        assertThat(log.getLoggingEvents(), iterableWithSize(0));
    }

    @Test
    void fallsbackProblemWithStatus() {
        ResponseEntity<Problem> result = AdviceTraits.fallback(
                Problem.valueOf(Status.RESET_CONTENT),
                new HttpHeaders()
        );
        assertThat(result.getStatusCode(), is(HttpStatus.RESET_CONTENT));
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(MediaType.valueOf("application/problem+json"));
        assertThat(result.getHeaders(), is(expectedHeaders));
        assertThat(result.getBody().getStatus(), is(Status.RESET_CONTENT));
    }

    @Test
    void fallsbackProblemWithoutStatus() {
        ResponseEntity<Problem> result = AdviceTraits.fallback(
                Problem.builder().withTitle("Some title").build(),
                new HttpHeaders()
        );
        assertThat(result.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR));
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(MediaType.valueOf("application/problem+json"));
        assertThat(result.getHeaders(), is(expectedHeaders));
        assertThat(result.getBody().getTitle(), is("Some title"));
    }

    @Test
    void findsJsonMediaType() {
        List<MediaType> mediaTypes = Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaTypes.X_PROBLEM);
        Optional<MediaType> mediaType = AdviceTraits.getProblemMediaType(mediaTypes);
        assertTrue(mediaType.isPresent());
        assertThat(mediaType.get(), is(MediaTypes.PROBLEM));
    }

    @Test
    void findsProblemMediaType() {
        List<MediaType> mediaTypes = Arrays.asList(MediaType.TEXT_PLAIN, MediaTypes.PROBLEM, MediaTypes.X_PROBLEM);
        Optional<MediaType> mediaType = AdviceTraits.getProblemMediaType(mediaTypes);
        assertTrue(mediaType.isPresent());
        assertThat(mediaType.get(), is(MediaTypes.PROBLEM));
    }

    @Test
    void findsXProblemMediaType() {
        List<MediaType> mediaTypes = Arrays.asList(MediaType.TEXT_PLAIN, MediaTypes.X_PROBLEM, MediaTypes.PROBLEM);
        Optional<MediaType> mediaType = AdviceTraits.getProblemMediaType(mediaTypes);
        assertTrue(mediaType.isPresent());
        assertThat(mediaType.get(), is(MediaTypes.X_PROBLEM));
    }

    @Test
    void returnsEmptyIfNoProblemCompatibleMediaType() {
        List<MediaType> mediaTypes = Arrays.asList(MediaType.TEXT_PLAIN, MediaType.IMAGE_PNG);
        Optional<MediaType> mediaType = AdviceTraits.getProblemMediaType(mediaTypes);
        assertFalse(mediaType.isPresent());
    }
}
