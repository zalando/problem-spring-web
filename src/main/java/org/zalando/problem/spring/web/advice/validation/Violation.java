package org.zalando.problem.spring.web.advice.validation;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class Violation {

    private final String field;
    private final String message;
    private final String code;

    public Violation(final String field, final String message, final @Nullable String code) {
        this.field = field;
        this.message = message;
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }
}
