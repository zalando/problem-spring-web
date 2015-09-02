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
import org.springframework.http.MediaType;
import org.zalando.problem.springweb.advice.MethodNotAllowed;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MethodNotAllowedIT extends AdviceIT {

    @Override
    protected Object advice() {
        return new MethodNotAllowed() {};
    }

    @Test
    public void methodNotAllowed() throws Exception {
        mvc.perform(request(POST, URI_HANDLER_PROBLEM)
                .accept(MediaType.parseMediaType("application/x.bla+json"), MediaTypes.PROBLEM))
                .andDo(print())
                .andExpect(header().string("Content-Type", is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.title", is(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.status", is(HttpStatus.METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.detail", containsString("not supported")));
    }

}