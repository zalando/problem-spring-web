package org.zalando.problem.spring.web.advice.validation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.zalando.problem.ThrowableProblem;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.ws.rs.core.Response.StatusType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Immutable
@JsonIgnoreProperties(value = "cause", ignoreUnknown = true)
@JsonTypeName(ConstraintViolationProblem.TYPE_VALUE)
public final class ConstraintViolationProblem extends ThrowableProblem {

    private static final long serialVersionUID = 3650862257257377020L;

	public static final String TYPE_VALUE = "https://zalando.github.io/problem/constraint-violation";
    public static final URI TYPE = URI.create(TYPE_VALUE);

    private final StatusType status;
    private final String detail;
    private final List<Violation> violations;

    public ConstraintViolationProblem(final StatusType status, final List<Violation> violations) {
        this(status, null, new ArrayList<>(violations));
    }

    @JsonCreator
    public ConstraintViolationProblem(@JsonProperty("status") final StatusType status,
    		@JsonProperty("detail") @Nullable final String detail,
    		@JsonProperty("violations") final List<Violation> violations) {
        this.status = status;
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
    public StatusType getStatus() {
        return status;
    }

    @Override
    public String getDetail() {
        return detail;
    }

    public List<Violation> getViolations() {
        return violations;
    }

}
