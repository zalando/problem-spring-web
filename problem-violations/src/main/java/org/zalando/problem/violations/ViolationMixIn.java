package org.zalando.problem.violations;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.INTERNAL;

// TODO package private
@API(status = INTERNAL)
public interface ViolationMixIn {

    @JsonProperty("field")
    String getField();

    @JsonProperty("message")
    String getMessage();
}
