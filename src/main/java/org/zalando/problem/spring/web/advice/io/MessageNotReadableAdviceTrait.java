package org.zalando.problem.spring.web.advice.io;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response.Status;

/**
 * @see HttpMessageNotReadableException
 * @see Status#BAD_REQUEST
 */
public interface MessageNotReadableAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleMessageNotReadableException(
            final HttpMessageNotReadableException exception,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return create(Status.BAD_REQUEST, exception, request);
    }

}
