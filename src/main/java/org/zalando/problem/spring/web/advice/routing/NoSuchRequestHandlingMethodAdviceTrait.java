package org.zalando.problem.spring.web.advice.routing;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response.Status;

/**
 * @see NoSuchRequestHandlingMethodException
 * @see Status#NOT_FOUND
 */
public interface NoSuchRequestHandlingMethodAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleNoSuchRequestHandlingMethod(
            final NoSuchRequestHandlingMethodException exception,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return create(Status.NOT_FOUND, exception, request);
    }

}
