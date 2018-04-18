package org.zalando.problem.spring.web.advice.validation;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Violation {

    private final String field;
    private final String message;
    private String[] codes = {};


    public Violation(final String field, String[] codes, final String message) {
        this.field = field;
        this.message = message;
        this.codes = codes;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public String[] getCodes() {
        return codes;
    }
}
