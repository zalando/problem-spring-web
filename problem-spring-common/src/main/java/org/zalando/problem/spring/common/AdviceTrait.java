package org.zalando.problem.spring.common;

import org.apiguardian.api.API;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zalando.problem.*;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.MAINTAINED;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.zalando.problem.spring.common.Lists.lengthOfTrailingPartialSubList;

/**
 * <p>
 * Advice traits are simple interfaces that provide a single method with a default
 * implementation. They are used to provide {@link ExceptionHandler} implementations to be used in
 * Spring Controllers and/or in a {@link ControllerAdvice}. Clients can choose which traits they what to
 * use à la carte.
 * </p>
 * <p>
 * Advice traits are grouped in packages, based on they use cases. Every package has a composite advice trait that
 * bundles all traits of that package.
 * </p>
 *
 * @see ControllerAdvice
 * @see ExceptionHandler
 * @see Throwable
 * @see Exception
 * @see Problem
 */
@API(status = INTERNAL)
public interface AdviceTrait {

    default ThrowableProblem toProblem(final Throwable throwable) {
        final StatusType status = Optional.ofNullable(resolveResponseStatus(throwable))
                .<StatusType>map(ResponseStatusAdapter::new)
                .or(() -> Optional.ofNullable(resolveStatusFromErrorResponse(throwable)))
                .orElse(Status.INTERNAL_SERVER_ERROR);

        return toProblem(throwable, status);
    }

    default StatusType resolveStatusFromErrorResponse(final Throwable type) {
        if (!(type instanceof ErrorResponse)) return null;

        final HttpStatusCode code = ((ErrorResponse) type).getStatusCode();
        return Status.valueOf(code.value());
    }

    @API(status = MAINTAINED)
    default ResponseStatus resolveResponseStatus(final Throwable type) {
        @Nullable final ResponseStatus candidate = findMergedAnnotation(type.getClass(), ResponseStatus.class);
        return candidate == null && type.getCause() != null ? resolveResponseStatus(type.getCause()) : candidate;
    }

    default ThrowableProblem toProblem(final Throwable throwable, final StatusType status) {
        return toProblem(throwable, status, Problem.DEFAULT_TYPE);
    }

    default ThrowableProblem toProblem(final Throwable throwable, final StatusType status, final URI type) {
        final ThrowableProblem problem = prepare(throwable, status, type).build();
        final StackTraceElement[] stackTrace = createStackTrace(throwable);
        problem.setStackTrace(stackTrace);
        return problem;
    }

    default ProblemBuilder prepare(final Throwable throwable, final StatusType status, final URI type) {
        return Problem.builder()
                .withType(type)
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .withDetail(throwable.getMessage())
                .withCause(Optional.ofNullable(throwable.getCause())
                    .filter(cause -> isCausalChainsEnabled())
                    .map(this::toProblem)
                    .orElse(null));
    }

    default StackTraceElement[] createStackTrace(final Throwable throwable) {
        final Throwable cause = throwable.getCause();

        if (cause == null || !isCausalChainsEnabled()) {
            return throwable.getStackTrace();
        } else {

            final StackTraceElement[] next = cause.getStackTrace();
            final StackTraceElement[] current = throwable.getStackTrace();

            final int length = current.length - lengthOfTrailingPartialSubList(asList(next), asList(current));
            final StackTraceElement[] stackTrace = new StackTraceElement[length];
            System.arraycopy(current, 0, stackTrace, 0, length);
            return stackTrace;
        }
    }

    default boolean isCausalChainsEnabled() {
        return false;
    }

    default ResponseEntity<Problem> process(final ResponseEntity<Problem> entity) {
        return entity;
    }

}
