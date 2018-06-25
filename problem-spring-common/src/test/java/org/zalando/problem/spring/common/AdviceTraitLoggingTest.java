package org.zalando.problem.spring.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.io.IOException;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class AdviceTraitLoggingTest {

    private final TestLogger log = TestLoggerFactory.getTestLogger(AdviceTrait.class);

    private final AdviceTrait unit = new AdviceTrait() {
    };

    @BeforeEach
    @AfterEach
    void reset() {
        TestLoggerFactory.clear();
    }

    static HttpStatus[] data() {
        return HttpStatus.values();
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldLog4xxAsWarn(final HttpStatus status) {
        assumeTrue(status.is4xxClientError());
        unit.log(new RuntimeException("Test message"), null, status);

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        assertThat(event.getLevel(), is(Level.WARN));
        assertThat(event.getMessage(), is("{}: {}"));
        assertThat(event.getArguments(), contains(status.getReasonPhrase(), "Test message"));
        assertThat(event.getThrowable().orNull(), is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldLog5xxAsError(final HttpStatus status) {
        assumeTrue(status.is5xxServerError());
        final IOException throwable = new IOException();
        unit.log(throwable, null, status);

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        assertThat(event.getLevel(), is(Level.ERROR));
        assertThat(event.getMessage(), is(status.getReasonPhrase()));
        assertThat(event.getArguments(), emptyIterable());
        assertThat(event.getThrowable().orNull(), is(throwable));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldNotLogNon4xx5xxErrors(final HttpStatus status) {
        assumeFalse(status.is5xxServerError() || status.is4xxClientError());
        final IOException throwable = new IOException();
        unit.log(throwable, null, status);

        assertThat(log.getLoggingEvents(), iterableWithSize(0));
    }

}
