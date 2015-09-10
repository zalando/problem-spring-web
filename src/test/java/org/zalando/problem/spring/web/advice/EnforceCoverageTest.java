package org.zalando.problem.spring.web.advice;

import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.junit.Test;

@Hack
@OhNoYouDidnt
public final class EnforceCoverageTest {

    @Test
    public void shouldUseMediaTypesConstructor() {
        new MediaTypes();
    }

    @Test
    public void shouldUseResponsesConstructor() {
        new Responses();
    }

}