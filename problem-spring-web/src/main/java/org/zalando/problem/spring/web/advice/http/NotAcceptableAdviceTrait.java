package org.zalando.problem.spring.web.advice.http;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see HttpMediaTypeNotAcceptableException
 * @see Status#NOT_ACCEPTABLE
 */
@API(status = STABLE)
public interface NotAcceptableAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default ResponseEntity<Problem> handleMediaTypeNotAcceptable(
            final HttpMediaTypeNotAcceptableException exception,
            final NativeWebRequest request) {
        return create(Status.NOT_ACCEPTABLE, exception, request);
    }

}
