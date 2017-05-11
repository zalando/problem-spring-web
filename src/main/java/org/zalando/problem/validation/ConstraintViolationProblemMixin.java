package org.zalando.problem.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zalando.problem.spring.web.advice.validation.Violation;

import java.util.List;

// TODO package private
// TODO rename to MixIn
public interface ConstraintViolationProblemMixin {

    @JsonProperty("violations")
    List<Violation> getViolations();
}