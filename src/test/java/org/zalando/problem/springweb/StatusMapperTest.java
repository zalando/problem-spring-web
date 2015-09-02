package org.zalando.problem.springweb;

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
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatusMapperTest {

    @Test
    public void mapsStatus() {
        final HttpStatus expected = HttpStatus.CHECKPOINT;
        final Response.StatusType input = mock(Response.StatusType.class);
        when(input.getStatusCode()).thenReturn(expected.value());

        final HttpStatus actual = StatusMapper.map(input);

        assertThat(actual, is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnUnknownStatus() {
        final Response.StatusType input = mock(Response.StatusType.class);
        when(input.getStatusCode()).thenReturn(1337);

        StatusMapper.map(input);
    }

}
