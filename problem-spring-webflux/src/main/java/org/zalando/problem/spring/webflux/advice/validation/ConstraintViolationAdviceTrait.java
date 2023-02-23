package org.zalando.problem.spring.webflux.advice.validation;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import reactor.core.publisher.Mono;

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

    default Violation createViolation(final ConstraintViolation violation) {
        return new Violation(formatFieldName(violation.getPropertyPath().toString()), violation.getMessage());
    }

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleConstraintViolation(
            final ConstraintViolationException exception,
            final ServerWebExchange request) {

        final List<Violation> violations = exception.getConstraintViolations().stream()
                .map(this::createViolation)
                .collect(toList());

        return newConstraintViolationProblem(exception, violations, request);
    }

}
