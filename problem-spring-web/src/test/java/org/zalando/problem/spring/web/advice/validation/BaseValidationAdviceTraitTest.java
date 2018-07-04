package org.zalando.problem.spring.web.advice.validation;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

final class BaseValidationAdviceTraitTest {

    @Test
    void formatFieldNameShouldntFormatByDefault() {
        assertThat(new Unit().formatFieldName("user_name"), is("user_name"));
    }

    private static class Unit implements BaseValidationAdviceTrait {

    }

}
