package org.zalando.problem.violations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apiguardian.api.API;
import org.zalando.problem.StatusType;

import jakarta.annotation.Nullable;
import java.net.URI;
import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

// TODO package private
// TODO rename to MixIn
@API(status = INTERNAL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        defaultImpl = ConstraintViolationProblem.class,
        visible = true)
abstract class ConstraintViolationProblemMixIn {

    @JsonCreator
    ConstraintViolationProblemMixIn(@Nullable @JsonProperty("type") URI type,
                                    @Nullable @JsonProperty("status") StatusType status,
                                    @Nullable @JsonProperty("violations") List<Violation> violations) {
        throw new ConstraintViolationProblem(type, status, violations);
    }

    @JsonProperty("violations")
    abstract List<Violation> getViolations();
}
