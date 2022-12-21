package org.zalando.problem.spring.web.advice.validation;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see ConstraintViolationException
 * @see Violation
 * @see ConstraintViolationProblem
 * @see ConstraintViolationProblem#TYPE_VALUE
 * @see BaseValidationAdviceTrait#defaultConstraintViolationStatus()
 */
@API(status = STABLE)
public interface ConstraintViolationAdviceTrait extends BaseValidationAdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default ResponseEntity<Problem> handleConstraintViolation(
            final ConstraintViolationException exception,
            final NativeWebRequest request) {

        final List<Violation> violations = exception.getConstraintViolations().stream()
                .map(this::createViolation)
                .collect(toList());

        return newConstraintViolationProblem(exception, violations, request);
    }

    default Violation createViolation(final ConstraintViolation violation) {
        return new Violation(formatFieldName(violation.getPropertyPath().toString()), violation.getMessage());
    }

}
