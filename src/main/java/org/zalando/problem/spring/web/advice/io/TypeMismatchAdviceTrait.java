package org.zalando.problem.spring.web.advice.io;

import org.apiguardian.api.API;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see TypeMismatchException
 * @see Status#BAD_REQUEST
 */
@API(status = STABLE)
public interface TypeMismatchAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default ResponseEntity<Problem> handleTypeMismatch(
            final TypeMismatchException exception,
            final NativeWebRequest request) {
        return create(Status.BAD_REQUEST, exception, request);
    }
}
