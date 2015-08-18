package org.zalando.problemhandling;

/*
 * #%L
 * problem-handling
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

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.Response;

import static org.zalando.problemhandling.StatusMapper.map;

@ControllerAdvice
public interface ExceptionHandling {

    @ExceptionHandler
    default ResponseEntity<Problem> handleRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException exception) {
        return buildEntity(Response.Status.METHOD_NOT_ALLOWED, exception);
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleMediaTypeNotSupportedException(final HttpMediaTypeNotSupportedException exception) {
        return buildEntity(Response.Status.UNSUPPORTED_MEDIA_TYPE, exception);
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleMessageNotReadableException(final HttpMessageNotReadableException exception) {
        return buildEntity(Response.Status.BAD_REQUEST, exception);
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleNotFound(final NoHandlerFoundException exception) {
        return buildEntity(Response.Status.NOT_FOUND, exception);
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleProblem(final ThrowableProblem problem) {
        return ResponseEntity
                .status(map(problem.getStatus()))
                .contentType(MediaTypes.PROBLEM)
                .body(problem);
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleThrowable(final Throwable throwable) {
        return buildEntity(Response.Status.INTERNAL_SERVER_ERROR, throwable);
    }

    default ResponseEntity<Problem> buildEntity(final Response.Status status, final Throwable throwable) {
        return buildEntity(status, throwable.getMessage());
    }

    default ResponseEntity<Problem> buildEntity(final Response.Status status, final String message) {
        return ResponseEntity
                .status(map(status))
                .contentType(MediaTypes.PROBLEM)
                .body(Problem.valueOf(status, message));
    }

}
