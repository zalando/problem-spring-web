package org.zalando.problem.spring.common;

import org.apiguardian.api.API;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.zalando.problem.StatusType;

import java.util.Objects;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * An implementation of {@link StatusType} to map {@link HttpStatus}.
 */
@API(status = EXPERIMENTAL)
public final class HttpStatusAdapter implements StatusType {

    private HttpStatusCode status;

    private String reason;

    public HttpStatusAdapter(HttpStatusCode status) {
        this.status = status;
        if (status instanceof HttpStatus) {
            this.reason = ((HttpStatus) status).getReasonPhrase();
        }
    }

    @Override
    public int getStatusCode() {
        return status.value();
    }

    @Override
    public String getReasonPhrase() {
        return reason;
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
