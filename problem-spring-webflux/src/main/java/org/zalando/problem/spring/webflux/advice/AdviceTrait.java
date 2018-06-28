package org.zalando.problem.spring.webflux.advice;

import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.accept.HeaderContentTypeResolver;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.webflux.advice.custom.CustomAdviceTrait;
import org.zalando.problem.spring.webflux.advice.general.GeneralAdviceTrait;
import org.zalando.problem.spring.webflux.advice.http.HttpAdviceTrait;
import org.zalando.problem.spring.webflux.advice.io.IOAdviceTrait;
import org.zalando.problem.spring.webflux.advice.routing.RoutingAdviceTrait;
import org.zalando.problem.spring.webflux.advice.validation.ValidationAdviceTrait;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static javax.servlet.RequestDispatcher.ERROR_EXCEPTION;
import static org.apiguardian.api.API.Status.STABLE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.spring.common.MediaTypes.PROBLEM;
import static org.zalando.problem.spring.common.MediaTypes.X_PROBLEM;

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
@API(status = STABLE)
public interface AdviceTrait extends org.zalando.problem.spring.common.AdviceTrait {

    /**
     * Creates a {@link Problem problem} {@link ResponseEntity response} for the given {@link Throwable throwable}
     * by taking any {@link ResponseStatus} annotation on the exception type or one of the causes into account.
     *
     * @param throwable exception being caught
     * @param request incoming request
     * @return the problem response
     */
    default Mono<ResponseEntity<Problem>> create(final Throwable throwable, final ServerWebExchange request) {
        final ThrowableProblem problem = toProblem(throwable);
        return create(throwable, problem, request);
    }

    default Mono<ResponseEntity<Problem>> create(final StatusType status, final Throwable throwable,
            final ServerWebExchange request) {
        return create(status, throwable, request, new HttpHeaders());
    }

    default Mono<ResponseEntity<Problem>> create(final StatusType status, final Throwable throwable,
            final ServerWebExchange request, final HttpHeaders headers) {
        return create(throwable, toProblem(throwable, status), request, headers);
    }

    default Mono<ResponseEntity<Problem>> create(final StatusType status, final Throwable throwable,
            final ServerWebExchange request, final URI type) {
        return create(status, throwable, request, new HttpHeaders(), type);
    }

    default Mono<ResponseEntity<Problem>> create(final StatusType status, final Throwable throwable,
            final ServerWebExchange request, final HttpHeaders headers, final URI type) {
        return create(throwable, toProblem(throwable, status, type), request, headers);
    }

    default Mono<ResponseEntity<Problem>> create(final ThrowableProblem problem, final ServerWebExchange request) {
        return create(problem, request, new HttpHeaders());
    }

    default Mono<ResponseEntity<Problem>> create(final ThrowableProblem problem, final ServerWebExchange request,
            final HttpHeaders headers) {
        return create(problem, problem, request, headers);
    }

    default Mono<ResponseEntity<Problem>> create(final Throwable throwable, final Problem problem,
            final ServerWebExchange request) {
        return create(throwable, problem, request, new HttpHeaders());
    }

    default Mono<ResponseEntity<Problem>> create(final Throwable throwable, final Problem problem,
            final ServerWebExchange request, final HttpHeaders headers) {

        final HttpStatus status = HttpStatus.valueOf(Optional.ofNullable(problem.getStatus())
                .orElse(Status.INTERNAL_SERVER_ERROR)
                .getStatusCode());

        return log(throwable, problem, request, status)
                .doOnSuccess(it -> {
                    if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                        request.getAttributes().put(ERROR_EXCEPTION, throwable);
                    }
                })
                .then(Mono.justOrEmpty(negotiate(request)))
                .map(contentType -> ResponseEntity
                        .status(status)
                        .headers(headers)
                        .contentType(contentType)
                        .body(problem)
                )
                .switchIfEmpty(fallback(throwable, problem, request, headers))
                .flatMap(entity -> process(entity, request));
    }

    default Mono<Void> log(
            @SuppressWarnings("UnusedParameters") final Throwable throwable,
            @SuppressWarnings("UnusedParameters") final Problem problem,
            @SuppressWarnings("UnusedParameters") final ServerWebExchange request,
            final HttpStatus status) {
        log(throwable, problem, status);
        return Mono.empty();
    }

    default Optional<MediaType> negotiate(final ServerWebExchange request) {
        List<MediaType> mediaTypes = new HeaderContentTypeResolver().resolveMediaTypes(request);
        // TODO: Move to common
        for (final MediaType mediaType : mediaTypes) {
            if (mediaType.includes(APPLICATION_JSON) || mediaType.includes(PROBLEM)) {
                return Optional.of(PROBLEM);
            } else if (mediaType.includes(X_PROBLEM)) {
                return Optional.of(X_PROBLEM);
            }
        }

        return Optional.empty();
    }

    default Mono<ResponseEntity<Problem>> fallback(
            @SuppressWarnings("UnusedParameters") final Throwable throwable,
            @SuppressWarnings("UnusedParameters") final Problem problem,
            @SuppressWarnings("UnusedParameters") final ServerWebExchange request,
            @SuppressWarnings("UnusedParameters") final HttpHeaders headers) {
        return Mono.just(fallback(throwable, problem, headers));
    }

    default Mono<ResponseEntity<Problem>> process(
            final ResponseEntity<Problem> entity,
            @SuppressWarnings("UnusedParameters") final ServerWebExchange request) {
        return Mono.just(process(entity));
    }

}
