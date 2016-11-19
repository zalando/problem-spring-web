package org.zalando.problem.spring.web.advice;

import com.google.gag.annotation.remark.Hack;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.custom.CustomAdviceTrait;
import org.zalando.problem.spring.web.advice.general.GeneralAdviceTrait;
import org.zalando.problem.spring.web.advice.http.HttpAdviceTrait;
import org.zalando.problem.spring.web.advice.io.IOAdviceTrait;
import org.zalando.problem.spring.web.advice.routing.RoutingAdviceTrait;
import org.zalando.problem.spring.web.advice.validation.ValidationAdviceTrait;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Arrays.asList;
import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.zalando.problem.spring.web.advice.Lists.lengthOfTrailingPartialSubList;
import static org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM;
import static org.zalando.problem.spring.web.advice.MediaTypes.WILDCARD_JSON;
import static org.zalando.problem.spring.web.advice.MediaTypes.X_PROBLEM;

/**
 * <p>
 * Advice traits are simple interfaces that provide a single method with a default
 * implementation. They are used to provide {@link ExceptionHandler} implementations to be used in
 * {@link Controller Controllers} and/or in a {@link ControllerAdvice}. Clients can choose which traits they what to
 * use à la carte.
 * </p>
 * <p>
 * Advice traits are grouped in packages, based on they use cases. Every package has a composite advice trait that
 * bundles all traits of that package. Additionally there is one {@link ProblemHandling major composite advice trait}
 * that in turn bundles all other composites.
 * </p>
 *
 * @see ControllerAdvice
 * @see ExceptionHandler
 * @see Throwable
 * @see Exception
 * @see Problem
 * @see ProblemHandling
 * @see CustomAdviceTrait
 * @see GeneralAdviceTrait
 * @see HttpAdviceTrait
 * @see IOAdviceTrait
 * @see RoutingAdviceTrait
 * @see ValidationAdviceTrait
 */
public interface AdviceTrait {

    Logger LOG = LoggerFactory.getLogger(AdviceTrait.class);

    default ResponseEntity<Problem> create(final StatusType status, final Throwable throwable,
            final NativeWebRequest request) {
        return create(status, throwable, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> create(final StatusType status, final Throwable throwable,
            final NativeWebRequest request, final HttpHeaders headers) {
        return create(throwable, toProblem(throwable, status), request, headers);
    }

    default ThrowableProblem toProblem(final Throwable throwable, final StatusType status) {
        final Throwable cause = throwable.getCause();

        final ProblemBuilder builder = Problem.builder()
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .withDetail(throwable.getMessage());

        final StackTraceElement[] stackTrace;

        if (cause == null || !isCausalChainsEnabled()) {
            stackTrace = throwable.getStackTrace();
        } else {
            builder.withCause(toProblem(cause, status));

            final StackTraceElement[] next = cause.getStackTrace();
            final StackTraceElement[] current = throwable.getStackTrace();

            final int length = current.length - lengthOfTrailingPartialSubList(asList(next), asList(current));
            stackTrace = new StackTraceElement[length];
            System.arraycopy(current, 0, stackTrace, 0, length);
        }

        final ThrowableProblem problem = builder.build();
        problem.setStackTrace(stackTrace);
        return problem;
    }

    default boolean isCausalChainsEnabled() {
        return false;
    }

    default ResponseEntity<Problem> create(final ThrowableProblem problem, final NativeWebRequest request) {
        return create(problem, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> create(final ThrowableProblem problem, final NativeWebRequest request,
            final HttpHeaders headers) {
        return create(problem, problem, request, headers);
    }

    default ResponseEntity<Problem> create(final Throwable throwable, final Problem problem,
            final NativeWebRequest request) {
        return create(throwable, problem, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> create(final Throwable throwable, final Problem problem,
            final NativeWebRequest request, final HttpHeaders headers) {

        final HttpStatus status = HttpStatus.valueOf(firstNonNull(
                problem.getStatus(), Status.INTERNAL_SERVER_ERROR).getStatusCode());

        log(throwable, problem, request, status);

        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            request.setAttribute(ERROR_EXCEPTION, throwable, SCOPE_REQUEST);
        }

        return process(negotiate(request).map(contentType ->
                ResponseEntity.status(status)
                        .headers(headers)
                        .contentType(contentType)
                        .body(problem))
                .orElseGet(() -> fallback(throwable, problem, request, headers)));
    }

    default void log(
            @SuppressWarnings("UnusedParameters") final Throwable throwable,
            @SuppressWarnings("UnusedParameters") final Problem problem,
            @SuppressWarnings("UnusedParameters") final NativeWebRequest request,
            final HttpStatus status) {
        if (status.is4xxClientError()) {
            LOG.warn("{}: {}", status.getReasonPhrase(), throwable.getMessage());
        } else if (status.is5xxServerError()) {
            LOG.error(status.getReasonPhrase(), throwable);
        }
    }

    default ResponseEntity<Problem> fallback(
            @SuppressWarnings("UnusedParameters") final Throwable throwable,
            @SuppressWarnings("UnusedParameters") final Problem problem,
            @SuppressWarnings("UnusedParameters") final NativeWebRequest request,
            @SuppressWarnings("UnusedParameters") final HttpHeaders headers) {
        return ResponseEntity.status(NOT_ACCEPTABLE).body(null);
    }

    @SneakyThrows(HttpMediaTypeNotAcceptableException.class)
    default Optional<MediaType> negotiate(final NativeWebRequest request) {
        final HeaderContentNegotiationStrategy negotiator = new HeaderContentNegotiationStrategy();

        final List<MediaType> mediaTypes = negotiator.resolveMediaTypes(request);

        if (mediaTypes.isEmpty()) {
            return Optional.of(PROBLEM);
        }

        for (final MediaType mediaType : mediaTypes) {
            if (mediaType.includes(APPLICATION_JSON) || mediaType.includes(PROBLEM)) {
                return Optional.of(PROBLEM);
            } else if (mediaType.includes(X_PROBLEM)) {
                return Optional.of(X_PROBLEM);
            }
        }

        @Hack("Accepting application/something+json doesn't make you understand application/problem+json, " +
                "but a lot of clients miss to send it correctly")
        final boolean isNeitherAcceptingJsonNorProblemJsonButSomeVendorSpecificJson =
                mediaTypes.stream().anyMatch(WILDCARD_JSON::includes);

        if (isNeitherAcceptingJsonNorProblemJsonButSomeVendorSpecificJson) {
            return Optional.of(PROBLEM);
        }

        return Optional.empty();
    }

    default ResponseEntity<Problem> process(final ResponseEntity<Problem> entity) {
        return entity;
    }

}
