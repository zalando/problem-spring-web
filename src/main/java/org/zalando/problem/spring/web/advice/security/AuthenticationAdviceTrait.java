package org.zalando.problem.spring.web.advice.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response;

public interface AuthenticationAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleAuthentication(final AuthenticationException e,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return create(Response.Status.FORBIDDEN, e, request);
    }

}
