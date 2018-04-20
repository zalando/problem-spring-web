package org.zalando.problem.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apiguardian.api.API;
import org.zalando.problem.spring.web.advice.validation.Violation;

import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

// TODO package private
// TODO rename to MixIn
@API(status = INTERNAL)
public interface ConstraintViolationProblemMixin {

    @JsonProperty("violations")
    List<Violation> getViolations();
}
