package org.zalando.problem.spring.web.advice.validation;

/*
 * #%L
 * problem-handling
 * %%
 * Copyright (C) 2015 Zalando SE
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.MoreStatus;
import org.zalando.problem.Problem;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @see ConstraintViolationException
 * @see Violation
 * @see ConstraintViolationProblem
 * @see ConstraintViolationProblem#CONSTRAINT_VIOLATION_VALUE
 * @see MoreStatus#UNPROCESSABLE_ENTITY
 */
public interface ConstraintViolationAdviceTrait extends BaseValidationAdviceTrait {

    default Violation createViolation(final ConstraintViolation violation) {
        return new Violation(formatFieldName(violation.getPropertyPath().toString()), violation.getMessage());
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleConstraintViolation(
            final ConstraintViolationException exception,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {

        final List<Violation> violations = exception.getConstraintViolations().stream()
                .map(this::createViolation)
                .collect(toList());

        return newConstraintViolationProblem(exception, violations, request);
    }

}
