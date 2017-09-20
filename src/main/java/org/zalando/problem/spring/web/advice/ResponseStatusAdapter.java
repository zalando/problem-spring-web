package org.zalando.problem.spring.web.advice;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.zalando.problem.StatusType;

/**
 * An implementation of {@link StatusType} to map {@link ResponseStatus}.
 */
final class ResponseStatusAdapter implements StatusType {

    private final ResponseStatus status;

    ResponseStatusAdapter(final ResponseStatus status) {
        this.status = status;
    }

    @Override
    public int getStatusCode() {
        return status.code().value();
    }

    @Override
    public String getReasonPhrase() {
        return status.reason().isEmpty() ? status.value().getReasonPhrase() : status.reason();
    }

}
