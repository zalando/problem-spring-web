package org.zalando.problem.spring.web.advice.validation;

/*
 * #%L
 * problem-spring-web
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.zalando.problem.MoreStatus;
import org.zalando.problem.ThrowableProblem;

import javax.annotation.concurrent.Immutable;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Immutable
@JsonTypeName(ConstraintViolationProblem.CONSTRAINT_VIOLATION_VALUE)
public final class ConstraintViolationProblem extends ThrowableProblem {

    public static final String CONSTRAINT_VIOLATION_VALUE = "https://github.com/zalando/problem/wiki/constraint-violation";
    public static final URI CONSTRAINT_VIOLATION = URI.create(CONSTRAINT_VIOLATION_VALUE);

    private final Optional<String> detail;
    private final List<Violation> violations;

    public ConstraintViolationProblem(final List<Violation> violations) {
        this(Optional.empty(), violations);
    }

    @JsonCreator
    private ConstraintViolationProblem(final Optional<String> detail, final List<Violation> violations) {
        this.detail = detail;
        this.violations = Collections.unmodifiableList(violations);
    }

    @Override
    public URI getType() {
        return CONSTRAINT_VIOLATION;
    }

    @Override
    public String getTitle() {
        return "Constraint Violation";
    }

    @Override
    public MoreStatus getStatus() {
        return MoreStatus.UNPROCESSABLE_ENTITY;
    }

    @Override
    public Optional<String> getDetail() {
        return detail;
    }

    public List<Violation> getViolations() {
        return violations;
    }

}
