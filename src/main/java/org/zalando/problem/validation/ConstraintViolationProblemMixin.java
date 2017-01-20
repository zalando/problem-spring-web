package org.zalando.problem.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.zalando.problem.spring.web.advice.validation.Violation;

import java.util.List;

public interface ConstraintViolationProblemMixin {

    @JsonProperty("violations")
    List<Violation> getViolations();
}