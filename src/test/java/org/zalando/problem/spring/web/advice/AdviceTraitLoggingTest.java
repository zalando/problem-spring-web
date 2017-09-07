package org.zalando.problem.spring.web.advice;

import com.google.gag.annotation.remark.Hack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.zalando.problem.Status;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.io.IOException;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.mock;

final class AdviceTraitLoggingTest {

    private final TestLogger log = TestLoggerFactory.getTestLogger(AdviceTrait.class);

    private final AdviceTrait unit = new AdviceTrait() {
    };

    @BeforeEach
    @AfterEach
    void reset() {
        TestLoggerFactory.clear();
    }

    static Status[] data() {
        return Status.values();
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldLog4xxAsWarn(final Status status) {
        assumeTrue(status.getStatusCode() / 100 == 4);
        unit.create(status, new NoHandlerFoundException("GET", "/", new HttpHeaders()), mock(NativeWebRequest.class));

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        assertThat(event.getLevel(), is(Level.WARN));
        assertThat(event.getMessage(), is("{}: {}"));
        assertThat(event.getArguments(), contains(getReasonPhrase(status), "No handler found for GET /"));
        assertThat(event.getThrowable().orNull(), is(nullValue()));
    }

    @ParameterizedTest
    @MethodSource("data")
    void shouldLog5xxAsError(final Status status) {
        assumeTrue(status.getStatusCode() / 100 == 5);
        final IOException throwable = new IOException();
        unit.create(status, throwable, mock(NativeWebRequest.class));

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        assertThat(event.getLevel(), is(Level.ERROR));
        assertThat(event.getMessage(), is(getReasonPhrase(status)));
        assertThat(event.getArguments(), emptyIterable());
        assertThat(event.getThrowable().orNull(), is(throwable));
    }

    @Hack("Because several status codes are defined with different reason phrases in Spring and JAX-RS")
    private String getReasonPhrase(final Status status) {
        return HttpStatus.valueOf(status.getStatusCode()).getReasonPhrase();
    }

}
