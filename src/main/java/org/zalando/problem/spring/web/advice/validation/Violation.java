package org.zalando.problem.spring.web.advice.validation;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Immutable
public final class Violation {

    private final String field;
    private final String message;

    @JsonCreator
    public Violation(@JsonProperty("field") final String field,
    		@JsonProperty("message")  final String message) {
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
