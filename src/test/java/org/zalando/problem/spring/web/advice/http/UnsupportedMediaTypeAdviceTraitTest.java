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
import org.zalando.problem.spring.web.advice.AdviceTraitTesting;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class UnsupportedMediaTypeAdviceTraitTest implements AdviceTraitTesting {

    @Test
    public void unsupportedMediaType() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/handler-put")
                .contentType("application/atom+xml"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(header().string("Accept", containsString("application/json")))
                .andExpect(jsonPath("$.type", is("http://httpstatus.es/415")))
                .andExpect(jsonPath("$.title", is("Unsupported Media Type")))
                .andExpect(jsonPath("$.status", is(415)))
                .andExpect(jsonPath("$.detail", containsString("application/atom+xml")));
    }

    @Test
    public void acceptHeaderIfSupported() throws Exception {
        mvc().perform(request(PUT, "http://localhost/api/handler-put")
                .contentType("application/atom+xml"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(header().string("Accept", containsString("application/json")))
                .andExpect(header().string("Accept", containsString("application/xml")));
    }

}
