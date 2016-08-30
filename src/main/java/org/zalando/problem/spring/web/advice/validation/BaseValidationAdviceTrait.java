package org.zalando.problem.spring.web.advice.validation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import java.util.Collection;
import java.util.List;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

interface BaseValidationAdviceTrait extends AdviceTrait {

    /**
     * Format the name of a violating field (e.g. lower camel to snake case)
     *
     * @param fieldName the field name to format
     * @return the formatted field name, defaults to the parameter, i.e. doesn't apply any transformation
     */
    default String formatFieldName(String fieldName) {
        return fieldName;
    }

    default ResponseEntity<Problem> newConstraintViolationProblem(final Throwable throwable,
        final Collection<Violation> stream, final NativeWebRequest request)
        throws HttpMediaTypeNotAcceptableException {

        final List<Violation> violations = stream.stream()
            // sorting to make tests deterministic
            .sorted(comparing(Violation::getField).thenComparing(Violation::getMessage))
            .collect(toList());

        return create(throwable, new ConstraintViolationProblem(violations), request);
    }

}
