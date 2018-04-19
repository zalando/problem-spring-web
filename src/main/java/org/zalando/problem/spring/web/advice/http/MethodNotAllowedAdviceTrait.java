package org.zalando.problem.spring.web.advice.http;

import com.google.gag.annotation.remark.Facepalm;
import com.google.gag.annotation.remark.WTF;
import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.annotation.Nullable;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public interface MethodNotAllowedAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default ResponseEntity<Problem> handleRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException exception,
            final NativeWebRequest request) {

        @WTF
        @Facepalm("Nullable arrays... great work from Spring :/")
        @Nullable final String[] methods = exception.getSupportedMethods();

        if (methods == null || methods.length == 0) {
            return create(Status.METHOD_NOT_ALLOWED, exception, request);
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setAllow(exception.getSupportedHttpMethods());

        return create(Status.METHOD_NOT_ALLOWED, exception, request, headers);
    }

}
