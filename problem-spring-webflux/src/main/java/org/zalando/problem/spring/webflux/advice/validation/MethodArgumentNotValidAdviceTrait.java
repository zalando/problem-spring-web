package org.zalando.problem.spring.webflux.advice.validation;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see MethodArgumentNotValidException
 * @see Violation
 * @see ConstraintViolationProblem
 * @see ConstraintViolationProblem#TYPE_VALUE
 * @see BaseValidationAdviceTrait#defaultConstraintViolationStatus()
 */
@API(status = STABLE)
public interface MethodArgumentNotValidAdviceTrait extends BaseBindingResultAdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception,
            final ServerWebExchange request) {
        return newConstraintViolationProblem(exception, createViolations(exception.getBindingResult()), request);
    }

}
