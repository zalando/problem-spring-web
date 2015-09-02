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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.DispatcherServlet;
import org.zalando.problem.springweb.advice.NotFound;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NotFoundIT extends AdviceIT {

    @Override
    protected Object advice() {
        return new NotFound() {};
    }

    @Test
    public void noHandlerInController() throws Exception {
        throwExceptionIfNoHandlerFound();

        mvc.perform(request(GET, URI_HANDLER_NO_MAPPING))
                .andExpect(header().string("Content-Type", is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(HttpStatus.NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    public void noHandler() throws Exception {
        throwExceptionIfNoHandlerFound();

        mvc.perform(request(GET, URI_NO_MAPPING))
                .andExpect(header().string("Content-Type", is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is(HttpStatus.NOT_FOUND.getReasonPhrase())))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_FOUND.value())));
    }

    private void throwExceptionIfNoHandlerFound() throws Exception {
        final Field field = MockMvc.class.getDeclaredField("servlet");
        field.setAccessible(true);
        final DispatcherServlet servlet = (DispatcherServlet) field.get(mvc);
        servlet.setThrowExceptionIfNoHandlerFound(true);
    }

}