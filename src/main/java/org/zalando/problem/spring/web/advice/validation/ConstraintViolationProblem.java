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
import com.google.common.collect.ImmutableList;
import org.zalando.problem.MoreStatus;
import org.zalando.problem.ThrowableProblem;

import javax.annotation.concurrent.Immutable;
import java.net.URI;
import java.util.Optional;

@Immutable
@JsonTypeName(ConstraintViolationProblem.TYPE_VALUE)
public final class ConstraintViolationProblem extends ThrowableProblem {

    public static final String TYPE_VALUE = "https://zalando.github.io/problem/constraint-violation";
    public static final URI TYPE = URI.create(TYPE_VALUE);

    private final Optional<String> detail;
    private final ImmutableList<Violation> violations;

    public ConstraintViolationProblem(final ImmutableList<Violation> violations) {
        this(Optional.empty(), violations);
    }

    @JsonCreator
    private ConstraintViolationProblem(final Optional<String> detail, final ImmutableList<Violation> violations) {
        this.detail = detail;
        this.violations = violations;
    }

    @Override
    public URI getType() {
        return TYPE;
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

    public ImmutableList<Violation> getViolations() {
        return violations;
    }

}
