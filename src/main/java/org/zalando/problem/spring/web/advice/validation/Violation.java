package org.zalando.problem.spring.web.advice.validation;

import org.apiguardian.api.API;

import javax.annotation.concurrent.Immutable;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@Immutable
public final class Violation {

    private final String field;
    private final String message;

    public Violation(final String field, final String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

}
