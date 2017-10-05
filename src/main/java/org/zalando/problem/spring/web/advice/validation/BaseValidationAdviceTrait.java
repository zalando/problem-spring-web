package org.zalando.problem.spring.web.advice.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.StatusType;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import java.net.URI;
import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.zalando.problem.Status.BAD_REQUEST;

interface BaseValidationAdviceTrait extends AdviceTrait {

    default URI defaultConstraintViolationType() {
        return ConstraintViolationProblem.TYPE;
    }

    default StatusType defaultConstraintViolationStatus() {
        return BAD_REQUEST;
    }

    /**
     * Format the name of a violating field (e.g. lower camel to snake case)
     *
     * @param fieldName the field name to format
     * @return the formatted field name, defaults to the parameter, i.e. doesn't apply any transformation
     */
    default String formatFieldName(final String fieldName) {
        return fieldName;
    }

    default ResponseEntity<Problem> newConstraintViolationProblem(final Throwable throwable,
        final Collection<Violation> stream, final NativeWebRequest request) {

        final URI type = defaultConstraintViolationType();
        final StatusType status = defaultConstraintViolationStatus();

        final List<Violation> violations = stream.stream()
            // sorting to make tests deterministic
            .sorted(comparing(Violation::getField).thenComparing(Violation::getMessage).thenComparing(Violation::getCode))
            .collect(toList());

        final Problem problem = new ConstraintViolationProblem(type, status, violations);

        return create(throwable, problem, request);
    }

}
