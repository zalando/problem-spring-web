package org.zalando.problem.spring.web.advice;

import com.google.common.collect.Iterables;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.web.context.request.NativeWebRequest;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;
import uk.org.lidalia.slf4jtest.TestLoggerFactoryResetRule;

import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.Arrays;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
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

    @Parameters(name= "{0}")
    public static Iterable<Status> data() {
        return Arrays.stream(Status.values())
                .filter(status -> status.getFamily() == Status.Family.SERVER_ERROR)
                .collect(toList());
    }

    @Test
    public void logs() {
        final IOException exception = new IOException();

        unit.create(status, exception, mock(NativeWebRequest.class));

        final LoggingEvent event = Iterables.getOnlyElement(log.getLoggingEvents());
        assertThat(event.getThrowable().orNull(), is(exception));
        assertThat(event.getMessage(), equalToIgnoringCase(status.getReasonPhrase()));
    }

}