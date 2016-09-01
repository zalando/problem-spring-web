package org.zalando.problem.spring.web.advice.validation;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConstraintViolationProblemTest {

    @Test
    public void shouldCreateCopyOfViolations(){
        final List<Violation> violations = new ArrayList<>();
        violations.add(new Violation("x", "y"));

        final ConstraintViolationProblem p = new ConstraintViolationProblem(violations);

        violations.clear();

        assertThat(p.getViolations(), hasSize(1));
        assertThat(p.getViolations().get(0).getField(), is("x"));
        assertThat(p.getViolations().get(0).getMessage(), is("y"));
    }
}
