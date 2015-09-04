package org.zalando.problem.springweb.advice;

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
import org.zalando.problem.springweb.MediaTypes;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MediaTypeNotAcceptableIT extends AdviceIT {

    @Override
    protected Object advice() {
        return new MediaTypeNotAcceptable() {
        };
    }

    @Test
    public void notAcceptable() throws Exception {
        mvc.perform(request(GET, URI_HANDLER_OK)
                .accept(MediaType.parseMediaType("application/x.vnd.specific+json")))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(jsonPath("$.title", is(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())))
                .andExpect(jsonPath("$.status", is(HttpStatus.NOT_ACCEPTABLE.value())))
                .andExpect(jsonPath("$.detail", containsString("Could not find acceptable representation")));
    }

    @Test
    public void notAcceptableNoProblem() throws Exception {
        mvc.perform(request(GET, URI_HANDLER_OK)
                .accept(MediaType.APPLICATION_ATOM_XML))
                .andExpect(status().isNotAcceptable())
                .andExpect(header().doesNotExist("Content-Type"));
    }

}