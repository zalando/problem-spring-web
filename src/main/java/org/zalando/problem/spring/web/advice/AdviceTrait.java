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
import org.zalando.problem.spring.web.advice.custom.CustomAdviceTrait;
import org.zalando.problem.spring.web.advice.general.GeneralAdviceTrait;
import org.zalando.problem.spring.web.advice.http.HttpAdviceTrait;
import org.zalando.problem.spring.web.advice.io.IOAdviceTrait;
import org.zalando.problem.spring.web.advice.routing.RoutingAdviceTrait;
import org.zalando.problem.spring.web.advice.validation.ValidationAdviceTrait;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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

    default ResponseEntity<Problem> create(final Response.StatusType status, final Throwable throwable,
            final NativeWebRequest request,
            final Function<ResponseEntity.BodyBuilder, ResponseEntity.BodyBuilder> buildable) throws HttpMediaTypeNotAcceptableException {
        return create(status, throwable.getMessage(), request, buildable);
    }

    default ResponseEntity<Problem> create(final Response.StatusType status, final Throwable throwable,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return create(status, throwable, request, identity());
    }

    default ResponseEntity<Problem> create(final Response.StatusType status, final String message,
            final NativeWebRequest request,
            final Function<ResponseEntity.BodyBuilder, ResponseEntity.BodyBuilder> buildable) throws HttpMediaTypeNotAcceptableException {
        return create(Problem.valueOf(status, message), request, buildable);
    }

    default ResponseEntity<Problem> create(final Response.StatusType status, final String message,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return create(status, message, request, identity());
    }

    default ResponseEntity<Problem> create(final Problem problem, final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return create(problem, request, identity());
    }

    default ResponseEntity<Problem> create(final Problem problem, final NativeWebRequest request,
            final Function<ResponseEntity.BodyBuilder, ResponseEntity.BodyBuilder> buildable) throws HttpMediaTypeNotAcceptableException {
        final HttpStatus status = HttpStatus.valueOf(problem.getStatus().getStatusCode());
        final ResponseEntity.BodyBuilder builder = buildable.apply(ResponseEntity.status(status));

        final Optional<MediaType> contentType = negotiate(request);

        if (contentType.isPresent()) {
            return builder
                    .contentType(contentType.get())
                    .body(problem);
        }

        return builder.body(null);
    }

    default Optional<MediaType> negotiate(final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        final HeaderContentNegotiationStrategy negotiator = new HeaderContentNegotiationStrategy();

            final List<MediaType> acceptedMediaTypes = negotiator.resolveMediaTypes(request);

            if (acceptedMediaTypes.isEmpty()) {
                return Optional.of(MediaTypes.PROBLEM);
            }

            if (acceptedMediaTypes.stream().anyMatch(MediaTypes.PROBLEM::isCompatibleWith)) {
                return Optional.of(MediaTypes.PROBLEM);
            }

            if (acceptedMediaTypes.stream().anyMatch(MediaTypes.X_PROBLEM::isCompatibleWith)) {
                return Optional.of(MediaTypes.X_PROBLEM);
            }

            if (acceptedMediaTypes.stream().anyMatch(MediaTypes.WILDCARD_JSON::isCompatibleWith)) {
                return Optional.of(MediaTypes.PROBLEM);
            }

            if (acceptedMediaTypes.stream().anyMatch(APPLICATION_JSON::isCompatibleWith)) {
                return Optional.of(MediaTypes.PROBLEM);
            }

        return Optional.empty();
    }

}
