package org.zalando.problem.violations;

import org.apiguardian.api.API;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

import javax.annotation.concurrent.Immutable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@Immutable
public final class ConstraintViolationProblem extends ThrowableProblem {

    public static final String TYPE_VALUE = "https://zalando.github.io/problem/constraint-violation";
    public static final URI TYPE = URI.create(TYPE_VALUE);

    private final URI type;
    private final StatusType status;
    private final List<Violation> violations;

    public ConstraintViolationProblem(final StatusType status, final List<Violation> violations) {
        this(TYPE, status, new ArrayList<>(violations));
    }

    public ConstraintViolationProblem(final URI type, final StatusType status, final List<Violation> violations) {
        this.type = type;
        this.status = status;
        this.violations = Collections.unmodifiableList(violations);
    }

    @Override
    public URI getType() {
        return type;
    }

    @Override
    public String getTitle() {
        return "Constraint Violation";
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

    public List<Violation> getViolations() {
        return violations;
    }

}
