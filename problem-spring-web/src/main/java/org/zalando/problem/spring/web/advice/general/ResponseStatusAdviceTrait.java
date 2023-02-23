package org.zalando.problem.spring.web.advice.general;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.common.HttpStatusAdapter;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * @see Throwable
 * @see Exception
 */
@API(status = EXPERIMENTAL)
public interface ResponseStatusAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default ResponseEntity<Problem> handleResponseStatusException(
            final ResponseStatusException exception,
            final NativeWebRequest request) {
        String reason = exception.getReason();
        return create(new HttpStatusAdapter(exception.getStatusCode()),exception, request);
    }

}
