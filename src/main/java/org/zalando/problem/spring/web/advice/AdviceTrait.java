package org.zalando.problem.spring.web.advice;

/*
 * #%L
 * problem-spring-web
 * %%
 * Copyright (C) 2015 Zalando SE
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

import javax.ws.rs.core.Response.StatusType;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
 * <p/>
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

    ResponseEntity<Problem> NOT_ACCEPTABLE = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null);

    default ResponseEntity<Problem> create(final StatusType status, final Throwable throwable,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return create(status, throwable, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> create(final StatusType status, final Throwable throwable,
            final NativeWebRequest request, final HttpHeaders headers)
            throws HttpMediaTypeNotAcceptableException {

        final ThrowableProblem problem = toProblem(throwable, status);
        return entity(problem, request, headers);
    }
    
    default ThrowableProblem toProblem(final Throwable throwable, final StatusType status) {
        final Throwable cause = throwable.getCause();

        final ProblemBuilder builder = Problem.builder()
                .withType(URI.create("about:blank"))
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

    default ResponseEntity<Problem> entity(final Problem problem, final NativeWebRequest request)
            throws HttpMediaTypeNotAcceptableException {
        return entity(problem, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> entity(final Problem problem, final NativeWebRequest request,
            final HttpHeaders headers) throws HttpMediaTypeNotAcceptableException {

        return negotiate(request).map(contentType -> {
            final int statusCode = problem.getStatus().getStatusCode();
            final HttpStatus status = HttpStatus.valueOf(statusCode);

            return ResponseEntity.status(status)
                    .headers(headers)
                    .contentType(contentType)
                    .body(problem);
        }).orElse(NOT_ACCEPTABLE);
    }

    default Optional<MediaType> negotiate(final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        final HeaderContentNegotiationStrategy negotiator = new HeaderContentNegotiationStrategy();
        final List<MediaType> mediaTypes = negotiator.resolveMediaTypes(request);

        if (mediaTypes.isEmpty()) {
            return Optional.of(PROBLEM);
        }

        for (final MediaType mediaType : mediaTypes) {
            if (APPLICATION_JSON.isCompatibleWith(mediaType)) {
                return Optional.of(PROBLEM);
            } else if (PROBLEM.isCompatibleWith(mediaType)) {
                return Optional.of(PROBLEM);
            } else if (X_PROBLEM.isCompatibleWith(mediaType)) {
                return Optional.of(X_PROBLEM);
            } else if (WILDCARD_JSON.isCompatibleWith(mediaType)) {
                return Optional.of(PROBLEM);
            }
        }

        return Optional.empty();
    }

}
