package org.zalando.problem.spring.web.advice.http;

/*
 * #%L
 * problem-spring-web
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class MethodNotAllowedAdviceTraitTest implements AdviceTraitTesting {

    @Test
    public void methodNotAllowed() throws Exception {
        mvc().perform(request(POST, "http://localhost/api/handler-problem")
                .accept("application/x.bla+json", "application/problem+json"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(header().string("Allow", is("GET")))
                .andExpect(jsonPath("$.type").doesNotExist())
                .andExpect(jsonPath("$.title", is("Method Not Allowed")))
                .andExpect(jsonPath("$.status", is(405)))
                .andExpect(jsonPath("$.detail", containsString("not supported")));
    }

    @Test
    public void noAllowIfNullAllowed() {
        final MethodNotAllowedAdviceTrait unit = new MethodNotAllowedAdviceTrait() {
        };
        final ResponseEntity<Problem> entity = unit.handleRequestMethodNotSupportedException(
                new HttpRequestMethodNotSupportedException("non allowed"), mock(NativeWebRequest.class));

        assertThat(entity.getHeaders(), not(hasKey("Allow")));
    }

    @Test
    public void noAllowIfNoneAllowed() {
        final MethodNotAllowedAdviceTrait unit = new MethodNotAllowedAdviceTrait() {
        };
        final ResponseEntity<Problem> entity = unit.handleRequestMethodNotSupportedException(
                new HttpRequestMethodNotSupportedException("non allowed", new String[]{}), mock(NativeWebRequest.class));

        assertThat(entity.getHeaders(), not(hasKey("Allow")));
    }

}
