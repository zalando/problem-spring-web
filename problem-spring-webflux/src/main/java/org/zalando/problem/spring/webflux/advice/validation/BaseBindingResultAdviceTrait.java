package org.zalando.problem.spring.webflux.advice.validation;

import org.apiguardian.api.API;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.zalando.problem.violations.Violation;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
// TODO: Move to common
public interface BaseBindingResultAdviceTrait extends BaseValidationAdviceTrait {

    default Violation createViolation(final FieldError error) {
        final String fieldName = formatFieldName(error.getField());
        return new Violation(fieldName, error.getDefaultMessage());
    }

    default Violation createViolation(final ObjectError error) {
        final String fieldName = formatFieldName(error.getObjectName());
        return new Violation(fieldName, error.getDefaultMessage());
    }

    default List<Violation> createViolations(final BindingResult result) {
        final Stream<Violation> fieldErrors = result.getFieldErrors().stream().map(this::createViolation);
        final Stream<Violation> globalErrors = result.getGlobalErrors().stream().map(this::createViolation);
        return Stream.concat(fieldErrors, globalErrors).collect(toList());
    }

}
