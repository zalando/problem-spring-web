package org.zalando.problem.spring.web.advice.example;


import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

import java.net.URI;

public class ExpectedProblem extends ThrowableProblem {

    private final String detail;

    public ExpectedProblem(final String detail) {
        this.detail = detail;
    }

    @Override
    public URI getType() {
        return URI.create("about:blank");
    }

    @Override
    public String getTitle() {
        return "Expected";
    }

    @Override
    public StatusType getStatus() {
        return Status.CONFLICT;
    }

    @Override
    public String getDetail() {
        return detail;
    }

}
