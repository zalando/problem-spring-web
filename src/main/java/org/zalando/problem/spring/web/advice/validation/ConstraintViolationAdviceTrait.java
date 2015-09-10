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

import com.google.common.collect.ImmutableList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.MoreStatus;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.Responses;

import javax.validation.ConstraintViolationException;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * @see ConstraintViolationException
 * @see Violation
 * @see ConstraintViolationProblem
 * @see ConstraintViolationProblem#CONSTRAINT_VIOLATION_VALUE
 * @see MoreStatus#UNPROCESSABLE_ENTITY
 */
public interface ConstraintViolationAdviceTrait extends BaseValidationAdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleConstraintViolation(
            final ConstraintViolationException exception,
            final NativeWebRequest request) {

        final ImmutableList<Violation> violations = exception.getConstraintViolations().stream()
                .map(error -> new Violation(formatFieldName(error.getPropertyPath().toString()), error.getMessage()))
                .sorted(comparing(Violation::getField).thenComparing(Violation::getMessage))
                .collect(collectingAndThen(toList(), ImmutableList::copyOf));

        return Responses.create(new ConstraintViolationProblem(violations), request);
    }

}
