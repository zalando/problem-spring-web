package org.zalando.problem.springweb.advice;

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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.springweb.advice.example.NotFoundException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class NotFoundAdviceTraitIT implements AdviceTraitTest<NotFoundAdviceTrait<NotFoundException>> {

    @ControllerAdvice
    private static class Advice implements NotFoundAdviceTrait<NotFoundException> {

    }

    @Override
    public NotFoundAdviceTrait<NotFoundException> unit() {
        return new Advice();
    }

    @Test
    public void missingRequestBody() throws Exception {
        mvc().perform(request(GET, "http://localhost/api/not-found"))
                .andExpect(header().string("Content-Type", is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type", is("http://httpstatus.es/404")))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.detail", containsString("Unable to find entity")));
    }

}
