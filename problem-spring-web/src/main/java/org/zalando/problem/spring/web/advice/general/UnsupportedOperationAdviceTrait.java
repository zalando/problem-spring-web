package org.zalando.problem.spring.web.advice.general;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see UnsupportedOperationException
 * @see Status#NOT_IMPLEMENTED
 */
@API(status = STABLE)
public interface UnsupportedOperationAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default ResponseEntity<Problem> handleUnsupportedOperation(
            final UnsupportedOperationException exception,
            final NativeWebRequest request) {
        return create(Status.NOT_IMPLEMENTED, exception, request);
    }

}
