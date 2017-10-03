package org.zalando.problem.spring.web.advice;

import com.google.gag.annotation.remark.Hack;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.custom.CustomAdviceTrait;
import org.zalando.problem.spring.web.advice.general.GeneralAdviceTrait;
import org.zalando.problem.spring.web.advice.http.HttpAdviceTrait;
import org.zalando.problem.spring.web.advice.io.IOAdviceTrait;
import org.zalando.problem.spring.web.advice.routing.RoutingAdviceTrait;
import org.zalando.problem.spring.web.advice.validation.ValidationAdviceTrait;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;
import static org.zalando.fauxpas.FauxPas.throwingSupplier;
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

    /**
     * Creates a {@link Problem problem} {@link ResponseEntity response} for the given {@link Throwable throwable}
     * by taking any {@link ResponseStatus} annotation on the exception type or one of the causes into account.
     *
     * @param throwable exception being caught
     * @param request incoming request
     * @return the problem response
     * @see ResponseStatusExceptionResolver
     */
    default ResponseEntity<Problem> create(final Throwable throwable, final NativeWebRequest request) {
        final ThrowableProblem problem = toProblem(throwable);
        return create(throwable, problem, request);
    }

    default ResponseEntity<Problem> create(final StatusType status, final Throwable throwable,
            final NativeWebRequest request) {
        return create(status, throwable, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> create(final StatusType status, final Throwable throwable,
            final NativeWebRequest request, final HttpHeaders headers) {
        return create(throwable, toProblem(throwable, status), request, headers);
    }

    default ResponseEntity<Problem> create(final StatusType status, final Throwable throwable,
            final NativeWebRequest request, final URI type) {
        return create(status, throwable, request, new HttpHeaders(), type);
    }

    default ResponseEntity<Problem> create(final StatusType status, final Throwable throwable,
            final NativeWebRequest request, final HttpHeaders headers, final URI type) {
        return create(throwable, toProblem(throwable, status, type), request, headers);
    }

    default ThrowableProblem toProblem(final Throwable throwable) {
        final StatusType status = Optional.ofNullable(resolveResponseStatus(throwable))
                .<StatusType>map(ResponseStatusAdapter::new)
                .orElse(Status.INTERNAL_SERVER_ERROR);

        return toProblem(throwable, status);
    }

    default ResponseStatus resolveResponseStatus(final Throwable type) {
        @Nullable final ResponseStatus candidate = findMergedAnnotation(type.getClass(), ResponseStatus.class);
        return candidate == null && type.getCause() != null ? resolveResponseStatus(type.getCause()) : candidate;
    }

    default StackTraceElement[] createStackTrace(final ProblemBuilder builder, final Throwable throwable) {
        final Throwable cause = throwable.getCause();
        final StackTraceElement[] stackTrace;

        if (cause == null || !isCausalChainsEnabled()) {
            stackTrace = throwable.getStackTrace();
        } else {
            builder.withCause(toProblem(cause));

            final StackTraceElement[] next = cause.getStackTrace();
            final StackTraceElement[] current = throwable.getStackTrace();

            final int length = current.length - lengthOfTrailingPartialSubList(asList(next), asList(current));
            stackTrace = new StackTraceElement[length];
            System.arraycopy(current, 0, stackTrace, 0, length);
        }

        return stackTrace;
    }

    default ThrowableProblem toProblem(final Throwable throwable, final StatusType status) {
        return toProblem(throwable, status, Problem.DEFAULT_TYPE);
    }

    default ThrowableProblem toProblem(final Throwable throwable, final StatusType status, final URI type) {
        final ProblemBuilder builder = newInitializedProblemBuilder(throwable, status, type);
        final StackTraceElement[] stackTrace = createStackTrace(builder, throwable);
        final ThrowableProblem problem = builder.build();

        problem.setStackTrace(stackTrace);
        return problem;
    }

    default ProblemBuilder newInitializedProblemBuilder(final Throwable throwable, final StatusType status, final URI type) {
        return Problem.builder()
                .withType(type)
                .withTitle(status.getReasonPhrase())
                .withStatus(status)
                .withDetail(throwable.getMessage());
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

        final HttpStatus status = HttpStatus.valueOf(Optional.ofNullable(problem.getStatus())
                .orElse(Status.INTERNAL_SERVER_ERROR)
                .getStatusCode());

        log(throwable, problem, request, status);

        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            request.setAttribute(ERROR_EXCEPTION, throwable, SCOPE_REQUEST);
        }

        return process(negotiate(request).map(contentType ->
                ResponseEntity.status(status)
                        .headers(headers)
                        .contentType(contentType)
                        .body(problem))
                .orElseGet(throwingSupplier(() -> {
                    final ResponseEntity<Problem> fallback = fallback(throwable, problem, request, headers);

                    if (fallback.getBody() == null) {
                        /*
                         * Ugly hack to workaround an issue with Tomcat and Spring as described in
                         * https://github.com/zalando/problem-spring-web/issues/84.
                         *
                         * The default fallback in case content negotiation failed is a 406 Not Acceptable without
                         * a body. Tomcat will then display its error page since no body was written and the response
                         * was not committed. In order to force Spring to flush/commit one would need to provide a
                         * body but that in turn would fail because Spring would then fail to negotiate the correct
                         * content type.
                         *
                         * Writing the status code, headers and flushing the body manually is a dirty way to bypass
                         * both parties, Tomcat and Spring, at the same time.
                         */
                        final ServerHttpResponse response = new ServletServerHttpResponse(
                                request.getNativeResponse(HttpServletResponse.class));

                        response.setStatusCode(fallback.getStatusCode());
                        response.getHeaders().putAll(fallback.getHeaders());
                        response.getBody(); // just so we're actually flushing the body...
                        response.flush();
                    }

                    return fallback;
                })), request);
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

    default ResponseEntity<Problem> fallback(
            @SuppressWarnings("UnusedParameters") final Throwable throwable,
            @SuppressWarnings("UnusedParameters") final Problem problem,
            @SuppressWarnings("UnusedParameters") final NativeWebRequest request,
            @SuppressWarnings("UnusedParameters") final HttpHeaders headers) {
        return ResponseEntity.status(NOT_ACCEPTABLE).body(null);
    }

    default ResponseEntity<Problem> process(
            final ResponseEntity<Problem> entity,
            @SuppressWarnings("UnusedParameters") final NativeWebRequest request) {
        return process(entity);
    }

    default ResponseEntity<Problem> process(final ResponseEntity<Problem> entity) {
        return entity;
    }

}
