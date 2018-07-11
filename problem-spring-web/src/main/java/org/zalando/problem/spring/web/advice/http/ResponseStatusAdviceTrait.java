package org.zalando.problem.spring.web.advice.http;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.SpringAdviceTrait;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see ResponseStatusException
 */
@API(status = STABLE)
public interface ResponseStatusAdviceTrait extends SpringAdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default ResponseEntity<Problem> handleResponseStatusException(
            final ResponseStatusException exception,
            final NativeWebRequest request) {
        return create(exception.getStatus(), exception, request);
    }

}
