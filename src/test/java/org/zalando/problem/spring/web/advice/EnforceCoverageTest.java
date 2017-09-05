package org.zalando.problem.spring.web.advice;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.jupiter.api.Test;

@Hack
@OhNoYouDidnt
final class EnforceCoverageTest {

    @Test
    void shouldUseListsConstructor() {
        new Lists();
    }

    @Test
    void shouldUseMediaTypesConstructor() {
        new MediaTypes();
    }

}
