package org.zalando.problem.spring.web.advice.validation;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class ConstraintViolationProblemTest {

    @Test
    public void shouldCreateCopyOfViolations(){
        final List<Violation> violations = new ArrayList<>();
        violations.add(new Violation("x", "y"));

        final ConstraintViolationProblem problem = new ConstraintViolationProblem(BAD_REQUEST, violations);

        violations.clear();

        assertThat(problem.getViolations(), hasSize(1));
        assertThat(problem.getViolations().get(0).getField(), is("x"));
        assertThat(problem.getViolations().get(0).getMessage(), is("y"));
    }
}
