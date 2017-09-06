package org.zalando.problem.spring.web.advice.general;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response.Status;

/**
 * @see Throwable
 * @see Exception
 * @see Status#INTERNAL_SERVER_ERROR
 */
public interface ThrowableAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleThrowable(
            final Throwable throwable,
            final NativeWebRequest request) {
        return create(throwable, request);
    }

}
