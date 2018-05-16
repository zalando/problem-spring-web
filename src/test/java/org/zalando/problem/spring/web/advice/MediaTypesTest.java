package org.zalando.problem.spring.web.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.core.Is.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM;
import static org.zalando.problem.spring.web.advice.MediaTypes.X_PROBLEM;

final class MediaTypesTest {

    @Test
    void isApplicationProblemJson() {
        assertThat(PROBLEM, hasFeature("Type", MediaType::getType, is("application")));
        assertThat(PROBLEM, hasFeature("Subtype", MediaType::getSubtype, is("problem+json")));
        assertThat(PROBLEM, hasFeature("Parameters", MediaType::getParameters, is(aMapWithSize(0))));
    }

    @Test
    void isApplicationXProblemJson() {
        assertThat(X_PROBLEM, hasFeature("Type", MediaType::getType, is("application")));
        assertThat(X_PROBLEM, hasFeature("Subtype", MediaType::getSubtype, is("x.problem+json")));
        assertThat(X_PROBLEM, hasFeature("Parameters", MediaType::getParameters, is(aMapWithSize(0))));
    }

    @Test
    void areCompatibleWithApplicationJsonWildcard() {
        assertThat(PROBLEM.isCompatibleWith(MediaType.parseMediaType("application/*+json")), is(true));
        assertThat(X_PROBLEM.isCompatibleWith(MediaType.parseMediaType("application/*+json")), is(true));
    }

}
