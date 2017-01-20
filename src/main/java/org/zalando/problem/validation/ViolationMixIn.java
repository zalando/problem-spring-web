package org.zalando.problem.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ViolationMixIn {

    @JsonProperty("field")
    String getField();

    @JsonProperty("message")
    String getMessage();
}
