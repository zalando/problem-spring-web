package org.zalando.problem.spring.web.advice;

/*
 * #%L
 * problem-handling
 * %%
 * Copyright (C) 2015 Zalando SE
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.core.Is.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM;
import static org.zalando.problem.spring.web.advice.MediaTypes.X_PROBLEM;

public final class MediaTypesTest {

    @Test
    public void isApplicationProblemJson() {
        assertThat(PROBLEM, hasFeature("Type", MediaType::getType, is("application")));
        assertThat(PROBLEM, hasFeature("Subtype", MediaType::getSubtype, is("problem+json")));
        assertThat(PROBLEM, hasFeature("Parameters", MediaType::getParameters, is(aMapWithSize(0))));
    }

    @Test
    public void isApplicationXProblemJson() {
        assertThat(X_PROBLEM, hasFeature("Type", MediaType::getType, is("application")));
        assertThat(X_PROBLEM, hasFeature("Subtype", MediaType::getSubtype, is("x.problem+json")));
        assertThat(X_PROBLEM, hasFeature("Parameters", MediaType::getParameters, is(aMapWithSize(0))));
    }

    @Test
    public void areCompatibleWithApplicationJsonWildcard() {
        assertThat(PROBLEM.isCompatibleWith(MediaType.parseMediaType("application/*+json")), is(true));
        assertThat(X_PROBLEM.isCompatibleWith(MediaType.parseMediaType("application/*+json")), is(true));
    }

}
