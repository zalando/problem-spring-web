package org.zalando.problem.spring.web.advice.validation;

import javax.annotation.concurrent.Immutable;

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
