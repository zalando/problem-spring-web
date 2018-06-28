package org.zalando.problem.violations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apiguardian.api.API;

import javax.annotation.Nullable;
import javax.validation.constraints.Null;

import static org.apiguardian.api.API.Status.INTERNAL;

// TODO package private
@API(status = INTERNAL)
abstract class ViolationMixIn {

    @JsonCreator
    ViolationMixIn(@Nullable @JsonProperty("field") String field,
                   @Nullable @JsonProperty("message") String message) {
    }

    @JsonProperty("field")
    abstract String getField();

    @JsonProperty("message")
    abstract String getMessage();
}
