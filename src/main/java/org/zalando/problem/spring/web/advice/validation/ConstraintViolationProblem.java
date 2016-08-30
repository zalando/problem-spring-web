package org.zalando.problem.spring.web.advice.validation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.zalando.problem.MoreStatus;
import org.zalando.problem.ThrowableProblem;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
@JsonTypeName(ConstraintViolationProblem.TYPE_VALUE)
public final class ConstraintViolationProblem extends ThrowableProblem {

    public static final String TYPE_VALUE = "https://zalando.github.io/problem/constraint-violation";
    public static final URI TYPE = URI.create(TYPE_VALUE);

    private final String detail;
    private final List<Violation> violations;

    public ConstraintViolationProblem(final List<Violation> violations) {
        this(null, new ArrayList<>(violations));
    }

    @JsonCreator
    ConstraintViolationProblem(@Nullable final String detail, final List<Violation> violations) {
        this.detail = detail;
        this.violations = Collections.unmodifiableList(violations);
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
    public String getDetail() {
        return detail;
    }

    public List<Violation> getViolations() {
        return violations;
    }

}
