package org.zalando.problem.violations;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.zalando.problem.Status.BAD_REQUEST;

public class ConstraintViolationProblemTest {

    @Test
    public void shouldCreateCopyOfViolations(){
        final List<Violation> violations = new ArrayList<>();
        violations.add(new Violation("x", "y"));

        final ConstraintViolationProblem problem = new ConstraintViolationProblem(BAD_REQUEST, violations);

        violations.clear();

        assertThat(problem.getViolations(), hasSize(1));
    }

}
