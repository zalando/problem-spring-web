package org.zalando.problem.spring.web.advice.validation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public interface BaseBindingResultAdviceTrait extends BaseValidationAdviceTrait {

    default Violation createViolation(final FieldError error) {
        final String fieldName = formatFieldName(error.getField());
        return new Violation(fieldName, error.getDefaultMessage());
    }

    default Violation createViolation(final ObjectError error) {
        final String fieldName = formatFieldName(error.getObjectName());
        return new Violation(fieldName, error.getDefaultMessage());
    }

    default List<Violation> createViolations(BindingResult bindingResult) {
      return Stream.concat(
              bindingResult.getFieldErrors().stream().map(this::createViolation),
              bindingResult.getGlobalErrors().stream().map(this::createViolation)).collect(toList());
    }

}
