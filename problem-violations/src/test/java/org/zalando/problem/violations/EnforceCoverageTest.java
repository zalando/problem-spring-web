package org.zalando.problem.violations;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.zalando.problem.Status.BAD_REQUEST;

@Hack
@OhNoYouDidnt
final class EnforceCoverageTest {

    @Test
    void shouldUseConstraintViolationProblemMixInConstructor() {
        final URI type = URI.create("https://example.org");
        assertThrows(ConstraintViolationProblem.class,
                () -> new ConstraintViolationProblemMixIn(type, BAD_REQUEST, null) {

                    @Override
                    List<Violation> getViolations() {
                        return Collections.emptyList();
                    }

                });
    }

    @Test
    void shouldUseViolationProblemMixInConstructor() {
        assertThrows(UnsupportedOperationException.class,
                () -> new ViolationMixIn("field", "message") {

                    @Override
                    String getField() {
                        return null;
                    }

                    @Override
                    String getMessage() {
                        return null;
                    }
                });
    }



}

