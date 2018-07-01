package org.zalando.problem.spring.common;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class AdviceTraitTest {

    private final AdviceTrait unit = new AdviceTrait() {
    };

    @ResponseStatus(HttpStatus.RESET_CONTENT)
    class TestException extends RuntimeException {
        public TestException(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.RESET_CONTENT, reason = "reason")
    class TestExceptionWithReason extends RuntimeException {
        public TestExceptionWithReason(String message) {
            super(message);
        }
    }

    @Test
    void buildsOnThrowable() {
        final Problem result = unit.toProblem(new IllegalStateException("Message"));

        assertThat(result.getStatus().getStatusCode(), is(500));
        assertThat(result.getType().toString(), is("about:blank"));
        assertThat(result.getTitle(), is("Internal Server Error"));
        assertThat(result.getDetail(), is("Message"));
    }

    @Test
    void buildsOnResponseStatusThrowable() {
        final Problem result = unit.toProblem(new TestException("Message"));

        assertThat(result.getStatus().getStatusCode(), is(205));
        assertThat(result.getType().toString(), is("about:blank"));
        assertThat(result.getTitle(), is("Reset Content"));
        assertThat(result.getDetail(), is("Message"));
    }

    @Test
    void buildsOnResponseStatusWithReasonThrowable() {
        final Problem result = unit.toProblem(new TestExceptionWithReason("Message"));

        assertThat(result.getStatus().getStatusCode(), is(205));
        assertThat(result.getType().toString(), is("about:blank"));
        assertThat(result.getTitle(), is("reason"));
        assertThat(result.getDetail(), is("Message"));
    }

    @Test
    void buildsOnNestedResponseStatusThrowable() {
        final Problem result = unit.toProblem(new RuntimeException("Message 1", new TestException("Message 2")));

        assertThat(result.getStatus().getStatusCode(), is(205));
        assertThat(result.getType().toString(), is("about:blank"));
        assertThat(result.getTitle(), is("Reset Content"));
        assertThat(result.getDetail(), is("Message 1"));
    }

    @Test
    void buildsOnThrowableWithStatus() {
        final Problem result = unit.toProblem(new IllegalStateException("Message"), Status.RESET_CONTENT);

        assertThat(result.getStatus().getStatusCode(), is(205));
        assertThat(result.getType().toString(), is("about:blank"));
        assertThat(result.getTitle(), is("Reset Content"));
        assertThat(result.getDetail(), is("Message"));
    }

    @Test
    void buildsOnThrowableWithStatusAndType() {
        final URI type = URI.create("https://google.com");
        final Problem result = unit.toProblem(new IllegalStateException("Message"), Status.RESET_CONTENT, type);

        assertThat(result.getStatus().getStatusCode(), is(205));
        assertThat(result.getType().toString(), is("https://google.com"));
        assertThat(result.getTitle(), is("Reset Content"));
        assertThat(result.getDetail(), is("Message"));
    }

    @Test
    void buildsStacktrace() {
        final Throwable throwable;

        try {
            try {
                try {
                    throw newNoSuchElement();
                } catch (final NoSuchElementException e) {
                    throw newIllegalArgument(e);
                }
            } catch (final IllegalArgumentException e) {
                throw newIllegalState(e);
            }
        } catch (final IllegalStateException e) {
            throwable = e;
        }

        final Problem entity = new AdviceTrait() {
            @Override
            public boolean isCausalChainsEnabled() {
                return true;
            }
        }.toProblem(throwable, Status.INTERNAL_SERVER_ERROR);

        assertThat(entity, is(instanceOf(ThrowableProblem.class)));

        final ThrowableProblem illegalState = (ThrowableProblem) entity;
        assertThat(illegalState.getType(), hasToString("about:blank"));
        assertThat(illegalState.getTitle(), is("Internal Server Error"));
        assertThat(illegalState.getStatus(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(illegalState.getDetail(), is("Illegal State"));
        assertThat(stacktraceAsString(illegalState).get(0), startsWith(method("newIllegalState")));
        assertThat(stacktraceAsString(illegalState).get(1), startsWith(method("buildsStacktrace")));
        assertThat(illegalState.getCause(), is(notNullValue()));

        final ThrowableProblem illegalArgument = illegalState.getCause();
        assertThat(illegalArgument.getType(), hasToString("about:blank"));
        assertThat(illegalArgument.getTitle(), is("Internal Server Error"));
        assertThat(illegalArgument.getStatus(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(illegalArgument.getDetail(), is("Illegal Argument"));
        assertThat(stacktraceAsString(illegalArgument).get(0), startsWith(method("newIllegalArgument")));
        assertThat(stacktraceAsString(illegalArgument).get(1), startsWith(method("buildsStacktrace")));
        assertThat(illegalArgument.getCause(), is(notNullValue()));

        final ThrowableProblem nullPointer = illegalArgument.getCause();
        assertThat(nullPointer.getType(), hasToString("about:blank"));
        assertThat(nullPointer.getTitle(), is("Internal Server Error"));
        assertThat(nullPointer.getStatus(), is(Status.INTERNAL_SERVER_ERROR));
        assertThat(nullPointer.getDetail(), is("No such element"));
        assertThat(stacktraceAsString(nullPointer).get(0), startsWith(method("newNoSuchElement")));
        assertThat(stacktraceAsString(nullPointer).get(1), startsWith(method("buildsStacktrace")));
        assertThat(nullPointer.getCause(), is(nullValue()));
    }

    private String method(final String s) {
        return "org.zalando.problem.spring.common.AdviceTraitTest." + s;
    }

    private List<String> stacktraceAsString(final Throwable throwable) {
        return Stream.of(throwable.getStackTrace())
                .map(Object::toString)
                .collect(toList());
    }

    private IllegalStateException newIllegalState(final Exception e) {
        throw new IllegalStateException("Illegal State", e);
    }

    private IllegalArgumentException newIllegalArgument(final Exception e) {
        throw new IllegalArgumentException("Illegal Argument", e);
    }

    private NoSuchElementException newNoSuchElement() {
        throw new NoSuchElementException("No such element");
    }

    @Test
    void fallsbackProblemWithStatus() {
        ResponseEntity<Problem> result = AdviceTrait.fallback(
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
        ResponseEntity<Problem> result = AdviceTrait.fallback(
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
    void processDoesNothing() {
        ResponseEntity<Problem> entity = ResponseEntity.ok(Problem.valueOf(Status.RESET_CONTENT));
        ResponseEntity<Problem> result = unit.process(entity);
        assertThat(result.getStatusCode(), is(HttpStatus.OK));
        assertThat(result.getBody().getStatus(), is(Status.RESET_CONTENT));
    }
}