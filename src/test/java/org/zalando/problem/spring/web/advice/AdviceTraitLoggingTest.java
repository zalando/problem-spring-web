package org.zalando.problem.spring.web.advice;

import com.google.gag.annotation.remark.Hack;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;

import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.Arrays;

import static com.google.common.collect.Iterables.getOnlyElement;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SERVER_ERROR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public final class AdviceTraitLoggingTest {

    private final TestLogger log = TestLoggerFactory.getTestLogger(AdviceTrait.class);

    @Rule
    public final TestLoggerFactoryResetRule rule = new TestLoggerFactoryResetRule();

    @Parameter
    public Status status;

    private final AdviceTrait unit = new AdviceTrait() {
    };

    @Parameters(name = "{0}")
    public static Iterable<Status> data() {
        return Arrays.asList(Status.values());
    }

    @Test
    public void shouldLog4xxAsWarn() {
        assumeThat(status.getFamily(), is(CLIENT_ERROR));

        unit.create(status, new NoHandlerFoundException("GET", "/", new HttpHeaders()), mock(NativeWebRequest.class));

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        assertThat(event.getLevel(), is(Level.WARN));
        assertThat(event.getMessage(), is("{}: {}"));
        assertThat(event.getArguments(), contains(getReasonPhrase(status), "No handler found for GET /"));
        assertThat(event.getThrowable().orNull(), is(nullValue()));
    }

    @Test
    public void shouldLog5xxAsError() {
        assumeThat(status.getFamily(), is(SERVER_ERROR));

        final IOException throwable = new IOException();
        unit.create(status, throwable, mock(NativeWebRequest.class));

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        assertThat(event.getLevel(), is(Level.ERROR));
        assertThat(event.getMessage(), is(getReasonPhrase(status)));
        assertThat(event.getArguments(), emptyIterable());
        assertThat(event.getThrowable().orNull(), is(throwable));
    }

    @Hack("Because several status codes are defined with different reason phrases in Spring and JAX-RS")
    public static String getReasonPhrase(final Status status) {
        return HttpStatus.valueOf(status.getStatusCode()).getReasonPhrase();
    }

}