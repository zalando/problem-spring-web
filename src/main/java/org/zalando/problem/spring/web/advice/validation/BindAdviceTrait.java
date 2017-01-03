package org.zalando.problem.spring.web.advice.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;

/**
 * @see BindException
 * @see Violation
 * @see ConstraintViolationProblem
 * @see ConstraintViolationProblem#TYPE_VALUE
 * @see BaseValidationAdviceTrait#defaultConstraintViolationStatus()
 */
public interface BindAdviceTrait extends BaseBindingResultAdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleBindingResult(
            final BindException exception,
            final NativeWebRequest request) {
        return newConstraintViolationProblem(exception, createViolations(exception), request);
    }
}
