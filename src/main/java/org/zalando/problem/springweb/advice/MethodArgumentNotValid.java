package org.zalando.problem.springweb.advice;

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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.springweb.problem.ConstraintViolationProblem;
import org.zalando.problem.springweb.problem.Violation;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.zalando.problem.springweb.advice.Responses.create;

public interface MethodArgumentNotValid extends AdviceTrait {

    /**
     * Format the name of a violating field (e.g. lower camel to snake case)
     */
    default String formatFieldName(final String fieldName) {
        return fieldName;
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception,
            final NativeWebRequest request) {

        final ImmutableList<Violation> violations = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(formatFieldName(error.getField()), error.getDefaultMessage()))
                .sorted(comparing(Violation::getField).thenComparing(Violation::getMessage))
                .collect(collectingAndThen(toList(), ImmutableList::copyOf));

        return create(new ConstraintViolationProblem(violations), request);
    }

    // TODO ConstraintViolationException?

}
