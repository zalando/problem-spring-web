package org.zalando.problem.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

// TODO package private
public interface ViolationMixIn {

    @JsonProperty("field")
    String getField();

    @JsonProperty("message")
    String getMessage();

    @JsonProperty("codes")
    String[] getCodes();
}
