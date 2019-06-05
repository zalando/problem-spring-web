package org.zalando.problem.spring.webflux.advice.validation;

import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.report.ValidationReport.Message;
import com.atlassian.oai.validator.springmvc.InvalidRequestException;
import com.atlassian.oai.validator.springmvc.InvalidResponseException;
import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.Violation;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;
import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = EXPERIMENTAL)
public interface OpenApiValidationAdviceTrait extends BaseValidationAdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleInvalidRequest(
            final InvalidRequestException exception,
            final ServerWebExchange request) {

        return newConstraintViolationProblem(exception, request, exception.getValidationReport());
    }

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleInvalidResponse(
            final InvalidResponseException exception,
            final ServerWebExchange request) {

        return newConstraintViolationProblem(exception, request, exception.getValidationReport());
    }

    default Mono<ResponseEntity<Problem>> newConstraintViolationProblem(final Exception exception,
            final ServerWebExchange request, final ValidationReport report) {

        final List<Violation> violations = report.getMessages().stream()
                .map(this::createViolation)
                .collect(toList());

        return newConstraintViolationProblem(exception, violations, request);
    }

    default Violation createViolation(final Message message) {
        return new Violation(message.getKey(), message.getMessage());
    }

}
