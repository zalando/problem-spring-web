package org.zalando.problem.spring.web.advice;


import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.compose;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.RESET_CONTENT;
import static org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM;
import static org.zalando.problem.spring.web.advice.MediaTypes.WILDCARD_JSON_VALUE;

public class AdviceTraitTest {

    private final AdviceTrait unit = new AdviceTrait() {
    };

    @Test
    public void buildsOnProblem() {
        final ThrowableProblem problem = mock(ThrowableProblem.class);
        when(problem.getStatus()).thenReturn(Status.RESET_CONTENT);

        final ResponseEntity<Problem> result = unit.create(problem, request());

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)));
    }

    @Test
    public void buildsOnThrowable() {
        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT,
                new IllegalStateException("Message"), request());

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is("Message"))));
    }

    @Test
    public void buildsOnThrowableWithType() {
        URI type = URI.create("https://google.com");
        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT,
          new IllegalStateException("Message"), request(), type);

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
          .and(hasFeature("Detail", Problem::getDetail, is("Message"))).and(hasFeature("Type", Problem::getType, is(type))));
    }

    @Test
    public void buildsOnMessage() {
        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT,
                new IllegalStateException("Message"), request());

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is("Message"))));
    }

    @Test
    public void buildsIfIncludes() {
        final String message = "Message";

        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT,
                new IllegalStateException(message),
                request(WILDCARD_JSON_VALUE));

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is(message))));
    }

    @Test
    public void buildsStacktrace() {
        final Throwable throwable;

        try {
            try {
                try {
                    throw newNullPointer();
                } catch (final NullPointerException e) {
                    throw newIllegalArgument(e);
                }
            } catch (final IllegalArgumentException e) {
                throw newIllegalState(e);
            }
        } catch (final IllegalStateException e) {
            throwable = e;
        }

        final ResponseEntity<Problem> entity = new AdviceTrait() {
            @Override
            public boolean isCausalChainsEnabled() {
                return true;
            }
        }.create(Status.INTERNAL_SERVER_ERROR, throwable, request());

        assertThat(entity.getBody(), is(instanceOf(ThrowableProblem.class)));

        final ThrowableProblem illegalState = (ThrowableProblem) entity.getBody();
        assertThat(illegalState.getType(), hasToString("https://httpstatuses.com/500"));
        assertThat(illegalState.getTitle(), is("Internal Server Error"));
        assertThat(illegalState.getStatus(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(illegalState.getDetail(), is("Illegal State"));
        assertThat(stacktraceAsString(illegalState).get(0), startsWith(method("newIllegalState")));
        assertThat(stacktraceAsString(illegalState).get(1), startsWith(method("buildsStacktrace")));
        assertThat(illegalState.getCause(), is(notNullValue()));

        final ThrowableProblem illegalArgument = illegalState.getCause();
        assertThat(illegalArgument.getType(), hasToString("https://httpstatuses.com/500"));
        assertThat(illegalArgument.getTitle(), is("Internal Server Error"));
        assertThat(illegalArgument.getStatus(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(illegalArgument.getDetail(), is("Illegal Argument"));
        assertThat(stacktraceAsString(illegalArgument).get(0), startsWith(method("newIllegalArgument")));
        assertThat(stacktraceAsString(illegalArgument).get(1), startsWith(method("buildsStacktrace")));
        assertThat(illegalArgument.getCause(), is(notNullValue()));

        final ThrowableProblem nullPointer = illegalArgument.getCause();
        assertThat(nullPointer.getType(), hasToString("https://httpstatuses.com/500"));
        assertThat(nullPointer.getTitle(), is("Internal Server Error"));
        assertThat(nullPointer.getStatus(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(nullPointer.getDetail(), is("Null Pointer"));
        assertThat(stacktraceAsString(nullPointer).get(0), startsWith(method("newNullPointer")));
        assertThat(stacktraceAsString(nullPointer).get(1), startsWith(method("buildsStacktrace")));
        assertThat(nullPointer.getCause(), is(nullValue()));
    }

    private String method(final String s) {
        return "org.zalando.problem.spring.web.advice.AdviceTraitTest." + s;
    }

    private List<String> stacktraceAsString(final Throwable throwable) {
        return Stream.of(throwable.getStackTrace())
                .map(Object::toString)
                .collect(toList());
    }

    private IllegalStateException newIllegalState(final IllegalArgumentException e) {
        throw new IllegalStateException("Illegal State", e);
    }

    private IllegalArgumentException newIllegalArgument(final NullPointerException e) {
        throw new IllegalArgumentException("Illegal Argument", e);
    }

    private NullPointerException newNullPointer() {
        throw new NullPointerException("Null Pointer");
    }

    @Test
    public void mapsStatus() {
        final HttpStatus expected = HttpStatus.BAD_REQUEST;
        final Response.StatusType input = Status.BAD_REQUEST;
        final ResponseEntity<Problem> entity = unit.create(input,
                new IllegalStateException("Checkpoint"), request());

        assertThat(entity.getStatusCode(), is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnUnknownStatus() {
        final Response.StatusType input = mock(Response.StatusType.class);
        when(input.getReasonPhrase()).thenReturn("L33t");
        when(input.getStatusCode()).thenReturn(1337);

        unit.create(input, new IllegalStateException("L33t"), request());
    }

    private NativeWebRequest request(final String acceptMediaType) {
        final NativeWebRequest request = mock(NativeWebRequest.class);
        when(request.getHeader("Accept")).thenReturn(acceptMediaType);
        return request;
    }

    private NativeWebRequest request() {
        return mock(NativeWebRequest.class);
    }

}