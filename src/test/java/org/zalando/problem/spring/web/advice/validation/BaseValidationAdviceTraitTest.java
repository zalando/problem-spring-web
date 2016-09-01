package org.zalando.problem.spring.web.advice.validation;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public final class BaseValidationAdviceTraitTest {

    @Test
    public void formatFieldNameShouldntFormatByDefault() {
        assertThat(new Unit().formatFieldName("user_name"), is("user_name"));
    }

    private static class Unit implements BaseValidationAdviceTrait {

    }

}