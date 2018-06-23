package org.zalando.problem.spring.web.advice;

import org.apiguardian.api.API;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.common.HttpStatusAdapter;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Spring version of {@link AdviceTrait} which uses {@link HttpStatus} rather than
 * {@link StatusType}.
 *
 * @see AdviceTrait
 */
@API(status = EXPERIMENTAL)
public interface SpringAdviceTrait extends AdviceTrait {

    default ResponseEntity<Problem> create(final HttpStatus status, final Throwable throwable,
            final NativeWebRequest request) {
        return create(status, throwable, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> create(final HttpStatus status, final Throwable throwable,
            final NativeWebRequest request, final HttpHeaders headers) {
        return create(toStatus(status), throwable, request, headers);
    }

    default ThrowableProblem toProblem(final Throwable throwable, final HttpStatus status) {
        return toProblem(throwable, toStatus(status));
    }

    default StatusType toStatus(final HttpStatus status) {
        return new HttpStatusAdapter(status);
    }

}
