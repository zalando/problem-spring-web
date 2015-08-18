package org.zalando.problemhandling;

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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.DispatcherServlet;
import org.zalando.problem.ProblemModule;
import org.zalando.problemhandling.example.ExampleRestController;

import java.lang.reflect.Field;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ExceptionHandlingIT {

    private static final String URI_HANDLER = "http://localhost/api/handler";
    private static final String URI_HANDLER_PROBLEM = "http://localhost/api/handler-problem";
    private static final String URI_HANDLER_THROWABLE = "http://localhost/api/handler-throwable";
    private static final String URI_HANDLER_NO_MAPPING = "http://localhost/api/no-handler";
    private static final String URI_NO_MAPPING = "http://localhost/no-handler";

    private final MockMvc mvc = standaloneSetup(new ExampleRestController())
            .setControllerAdvice(new ExceptionHandling() {
            })
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper()))
            .build();

    @Test
    public void methodNotAllowed() throws Exception {
        mvc.perform(request(POST, URI_HANDLER_PROBLEM))
                .andExpect(header().string("Content-Type", is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.title", is(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())))
                .andExpect(jsonPath("$.status", is(HttpStatus.METHOD_NOT_ALLOWED.value())))
                .andExpect(jsonPath("$.detail", containsString("not supported")));
    }

    @Test
    public void throwableProblem() throws Exception {
        mvc.perform(request(GET, URI_HANDLER_PROBLEM))
                .andExpect(header().string("Content-Type", is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("expected")))
                .andExpect(jsonPath("$.status", is(HttpStatus.CONFLICT.value())));
    }

    @Test
    public void throwable() throws Exception {
        mvc.perform(request(GET, URI_HANDLER_THROWABLE))
                .andExpect(header().string("Content-Type", is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title", is(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())))
                .andExpect(jsonPath("$.status", is(HttpStatus.INTERNAL_SERVER_ERROR.value())))
                .andExpect(jsonPath("$.detail", containsString("expected")));
    }

    @Test
    public void unsupportedContentType() throws Exception {
        mvc.perform(request(PUT, URI_HANDLER)
                .contentType(MediaType.APPLICATION_ATOM_XML))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.title", is(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())))
                .andExpect(jsonPath("$.status", is(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())))
                .andExpect(jsonPath("$.detail", containsString(MediaType.APPLICATION_ATOM_XML.toString())));
    }

    @Test
    public void missingRequestBody() throws Exception {
        mvc.perform(request(PUT, URI_HANDLER)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Content-Type", is(MediaTypes.PROBLEM_VALUE)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value())))
                .andExpect(jsonPath("$.detail", containsString("request body is missing")));
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

    private ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ProblemModule());
        objectMapper.registerModule(new Jdk8Module());
        return objectMapper;
    }

    private void throwExceptionIfNoHandlerFound() throws Exception {
        final Field field = MockMvc.class.getDeclaredField("servlet");
        field.setAccessible(true);
        final DispatcherServlet servlet = (DispatcherServlet) field.get(mvc);
        servlet.setThrowExceptionIfNoHandlerFound(true);
    }

}