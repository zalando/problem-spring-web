package org.zalando.problem.spring.common;

import org.apiguardian.api.API;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import java.util.List;
import java.util.Optional;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.spring.common.MediaTypes.PROBLEM;
import static org.zalando.problem.spring.common.MediaTypes.X_PROBLEM;

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

    public static Optional<MediaType> getProblemMediaType(final List<MediaType> mediaTypes) {
        for (final MediaType mediaType : mediaTypes) {
            if (mediaType.includes(APPLICATION_JSON) || mediaType.includes(PROBLEM)) {
                return Optional.of(PROBLEM);
            } else if (mediaType.includes(X_PROBLEM)) {
                return Optional.of(X_PROBLEM);
            }
        }

        return Optional.empty();
    }
}
