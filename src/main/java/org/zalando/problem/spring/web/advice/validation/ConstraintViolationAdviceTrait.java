package org.zalando.problem.spring.web.advice.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @see ConstraintViolationException
 * @see Violation
 * @see ConstraintViolationProblem
 * @see ConstraintViolationProblem#TYPE_VALUE
 * @see BaseValidationAdviceTrait#defaultConstraintViolationStatus()
 */
public interface ConstraintViolationAdviceTrait extends BaseValidationAdviceTrait {

    default Violation createViolation(final ConstraintViolation violation) {
        return new Violation(formatFieldName(violation.getPropertyPath().toString()), violation.getMessage(), null);
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleConstraintViolation(
            final ConstraintViolationException exception,
            final NativeWebRequest request) {

        final List<Violation> violations = exception.getConstraintViolations().stream()
                .map(this::createViolation)
                .collect(toList());

        return newConstraintViolationProblem(exception, violations, request);
    }

}
