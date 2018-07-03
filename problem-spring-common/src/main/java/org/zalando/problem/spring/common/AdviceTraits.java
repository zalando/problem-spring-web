package org.zalando.problem.spring.common;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.util.Optional;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.zalando.problem.spring.common.MediaTypes.PROBLEM;

@API(status = INTERNAL)
public final class AdviceTraits {

    private static final Logger LOG = LoggerFactory.getLogger(AdviceTrait.class);

    private AdviceTraits() {

    }

    public static void log(
            final Throwable throwable,
            final HttpStatus status) {
        if (status.is4xxClientError()) {
            LOG.warn("{}: {}", status.getReasonPhrase(), throwable.getMessage());
        } else if (status.is5xxServerError()) {
            LOG.error(status.getReasonPhrase(), throwable);
        }
    }

    public static ResponseEntity<Problem> fallback(
            final Problem problem,
            final HttpHeaders headers) {
        return ResponseEntity
                .status(HttpStatus.valueOf(Optional.ofNullable(problem.getStatus())
                        .orElse(Status.INTERNAL_SERVER_ERROR)
                        .getStatusCode()))
                .headers(headers)
                .contentType(PROBLEM)
                .body(problem);
    }
}
