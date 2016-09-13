package org.zalando.problem.spring.web.advice.general;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response.Status;

/**
 * @see UnsupportedOperationException
 * @see Status#NOT_IMPLEMENTED
 */
public interface UnsupportedOperationAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleUnsupportedOperation(
            final UnsupportedOperationException exception,
            final NativeWebRequest request) {
        return create(Status.NOT_IMPLEMENTED, exception, request);
    }

}
