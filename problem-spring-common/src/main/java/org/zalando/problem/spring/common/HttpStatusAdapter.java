package org.zalando.problem.spring.common;

import org.apiguardian.api.API;
import org.springframework.http.HttpStatus;
import org.zalando.problem.StatusType;

import java.util.Objects;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * An implementation of {@link StatusType} to map {@link HttpStatus}.
 */
@API(status = EXPERIMENTAL)
// TODO final
public class HttpStatusAdapter implements StatusType {

    private final HttpStatus status;

    public HttpStatusAdapter(final HttpStatus status) {
        this.status = status;
    }

    @Override
    public int getStatusCode() {
        return status.value();
    }

    @Override
    public String getReasonPhrase() {
        return status.getReasonPhrase();
    }

    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        } else if (that instanceof HttpStatusAdapter) {
            final HttpStatusAdapter other = (HttpStatusAdapter) that;
            return Objects.equals(status, other.status);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(status);
    }

}
