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

import javax.ws.rs.core.Response;

import static org.hamcrest.Matchers.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.junit.Assert.assertThat;

public class UnknownStatusTest {

    private final Response.StatusType unit = new UnknownStatus(1337);

    @Test
    public void isOtherFamily() {
        assertThat(unit, hasFeature("Family", Response.StatusType::getFamily, is(Response.Status.Family.OTHER)));
    }

    @Test
    public void isUnknownReasonPhrase() {
        assertThat(unit, hasFeature("Reason phrase", Response.StatusType::getReasonPhrase, is("Unknown")));
    }
}
