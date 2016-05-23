package org.zalando.problem.spring.web.advice;

/*
 * #%L
 * Problem: Spring Web
 * %%
 * Copyright (C) 2015 - 2016 Zalando SE
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

import static javax.ws.rs.core.Response.Status.Family;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public final class HttpStatusAdapterTest {

    @Test
    public void shouldMapHttpStatusProperties() {
        HttpStatusAdapter adapter = new HttpStatusAdapter(HttpStatus.I_AM_A_TEAPOT);

        assertThat(adapter.getStatusCode(), is(418));
        assertThat(adapter.getFamily(), is(Family.CLIENT_ERROR));
        assertThat(adapter.getReasonPhrase(), is(HttpStatus.I_AM_A_TEAPOT.getReasonPhrase()));
    }

    @Test
    public void shouldUseHttpStatusEqualsAndHashCode() {
        HttpStatus status = HttpStatus.I_AM_A_TEAPOT;
        HttpStatusAdapter adapter = new HttpStatusAdapter(HttpStatus.I_AM_A_TEAPOT);

        assertThat(adapter, is(adapter));
        assertThat(adapter, is(new HttpStatusAdapter(status)));
        assertThat(adapter, not(new HttpStatusAdapter(HttpStatus.BAD_GATEWAY)));
        assertThat(adapter, not(HttpStatus.I_AM_A_TEAPOT));
        assertThat(adapter.hashCode(), is(new HttpStatusAdapter(HttpStatus.I_AM_A_TEAPOT).hashCode()));
    }
}
