package org.zalando.problem.springweb.advice;

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
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;

import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static org.zalando.problem.springweb.advice.MediaTypes.determineContentType;
import static org.zalando.problem.springweb.advice.StatusMapper.map;

final class Responses {

    static ResponseEntity<Problem> create(final Response.StatusType status, final Throwable throwable,
            final NativeWebRequest request,
            final Function<ResponseEntity.BodyBuilder, ResponseEntity.BodyBuilder> buildable) {
        return create(status, throwable.getMessage(), request, buildable);
    }

    static ResponseEntity<Problem> create(final Response.StatusType status, final Throwable throwable,
            final NativeWebRequest request) {
        return create(status, throwable, request, identity());
    }

    static ResponseEntity<Problem> create(final Response.StatusType status, final String message,
            final NativeWebRequest request,
            final Function<ResponseEntity.BodyBuilder, ResponseEntity.BodyBuilder> buildable) {
        return create(Problem.valueOf(status, message), request, buildable);
    }

    static ResponseEntity<Problem> create(final Response.StatusType status, final String message,
            final NativeWebRequest request) {
        return create(status, message, request, identity());
    }

    static ResponseEntity<Problem> create(final Problem problem, final NativeWebRequest request,
            final Function<ResponseEntity.BodyBuilder, ResponseEntity.BodyBuilder> buildable) {
        final HttpStatus status = map(problem.getStatus());
        final ResponseEntity.BodyBuilder builder = buildable.apply(ResponseEntity.status(status));

        final Optional<MediaType> contentType = determineContentType(request);
        if (contentType.isPresent()) {
            return builder.contentType(contentType.get()).body(problem);
        }
        return builder.body(null);
    }

    static ResponseEntity<Problem> create(final Problem problem, final NativeWebRequest request) {
        return create(problem, request, identity());
    }

}
