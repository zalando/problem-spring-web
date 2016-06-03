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
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.MoreStatus;
import org.zalando.problem.Problem;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @see MethodArgumentNotValidException
 * @see Violation
 * @see ConstraintViolationProblem
 * @see ConstraintViolationProblem#CONSTRAINT_VIOLATION_VALUE
 * @see MoreStatus#UNPROCESSABLE_ENTITY
 */
public interface MethodArgumentNotValidAdviceTrait extends BaseValidationAdviceTrait {

    default Violation createViolation(final FieldError error) {
        final String fieldName = error.getObjectName() + "." + error.getField();
        return new Violation(formatFieldName(fieldName), error.getDefaultMessage());
    }

    default Violation createViolation(final ObjectError error) {
        final String fieldName = formatFieldName(error.getObjectName());
        return new Violation(fieldName, error.getDefaultMessage());
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {

        final List<Violation> violations = Stream.concat(
                exception.getBindingResult().getFieldErrors().stream().map(this::createViolation),
                exception.getBindingResult().getGlobalErrors().stream().map(this::createViolation))
                .collect(toList());

        return newConstraintViolationProblem(exception, violations, request);
    }

}
