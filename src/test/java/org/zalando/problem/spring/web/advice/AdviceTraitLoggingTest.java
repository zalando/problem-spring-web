package org.zalando.problem.spring.web.advice;

import com.google.gag.annotation.remark.Hack;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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

import javax.annotation.Nullable;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public final class AdviceTraitLoggingTest {

    private final TestLogger log = TestLoggerFactory.getTestLogger(AdviceTrait.class);

    @Rule
    public final TestLoggerFactoryResetRule rule = new TestLoggerFactoryResetRule();

    @Parameter
    public Assertion assertion;

    private final AdviceTrait unit = new AdviceTrait() {
    };

    @Value
    @RequiredArgsConstructor
    private static class Assertion {

        Status status;
        Level level;
        String message;
        Object[] arguments;
        Throwable thrown;
        Throwable logged;

    }

    @Parameters(name = "{0}")
    public static Iterable<Assertion> data() {
        return Arrays.stream(Status.values())
                .map(AdviceTraitLoggingTest::toAssertion)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @Nullable
    private static Assertion toAssertion(final Status status) {
        switch (status.getFamily()) {
            case CLIENT_ERROR:
                return new Assertion(status, Level.WARN, "{}: {}",
                        new Object[]{getReasonPhrase(status), "No handler found for GET /"},
                        new NoHandlerFoundException("GET", "/", new HttpHeaders()), null);
            case SERVER_ERROR:
                final IOException exception = new IOException();
                return new Assertion(status, Level.ERROR, getReasonPhrase(status), new Object[0],
                        exception, exception);
            default:
                return null;
        }
    }

    @Hack("Because several status codes are defined with different reason phrases in Spring and JAX-RS")
    public static String getReasonPhrase(final Status status) {
        return HttpStatus.valueOf(status.getStatusCode()).getReasonPhrase();
    }

    @Test
    public void logs() {
        unit.create(assertion.status, assertion.thrown, mock(NativeWebRequest.class));

        final LoggingEvent event = getOnlyElement(log.getLoggingEvents());
        assertThat(event.getLevel(), is(assertion.level));
        assertThat(event.getMessage(), is(assertion.message));
        assertThat(event.getArguments(),
                assertion.arguments.length == 0 ? emptyIterable() : contains(assertion.arguments));
        assertThat(event.getThrowable().orNull(), is(assertion.logged));
    }

}