package org.zalando.problem.spring.web.advice.io;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response.Status;

/**
 * @see TypeMismatchException
 * @see Status#BAD_REQUEST
 */
public interface TypeMistmatchAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleTypeMismatch(
            final TypeMismatchException exception,
            final NativeWebRequest request) {
        return create(Status.BAD_REQUEST, exception, request);
    }
}
