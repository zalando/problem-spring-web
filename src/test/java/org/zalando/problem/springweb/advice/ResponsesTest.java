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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.Response.Status;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.compose;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.RESET_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_ATOM_XML_VALUE;
import static org.zalando.problem.springweb.advice.MediaTypes.PROBLEM;
import static org.zalando.problem.springweb.advice.MediaTypes.WILDCARD_JSON_VALUE;

public class ResponsesTest {

    @Test
    public void buildsOnProblem() {
        final ThrowableProblem problem = mock(ThrowableProblem.class);
        when(problem.getStatus()).thenReturn(Status.RESET_CONTENT);

        final ResponseEntity<Problem> result = Responses.create(problem, request());

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)));
    }

    @Test
    public void buildsOnThrowable() {
        final String message = "Message";
        final Throwable throwable = mock(Throwable.class);
        when(throwable.getMessage()).thenReturn(message);

        final ResponseEntity<Problem> result = Responses.create(Status.RESET_CONTENT, throwable, request());

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is(Optional.of(message)))));
    }

    @Test
    public void buildsOnMessage() {
        final String message = "Message";

        final ResponseEntity<Problem> result = Responses.create(Status.RESET_CONTENT, message, request());

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is(Optional.of(message)))));
    }

    @Test
    public void buildsIfIncludes() {
        final String message = "Message";

        final ResponseEntity<Problem> result = Responses.create(Status.RESET_CONTENT, message,
                request(WILDCARD_JSON_VALUE));

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is(Optional.of(message)))));
    }

    @Test
    public void buildsEmptyIfNotIncludes() {
        final ResponseEntity<Problem> result = Responses.create(Status.RESET_CONTENT, "",
                request(APPLICATION_ATOM_XML_VALUE));

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders().getContentType(), is(nullValue()));
        assertThat(result.getBody(), is(nullValue()));
    }

    private NativeWebRequest request(String acceptMediaType) {
        final NativeWebRequest request = mock(NativeWebRequest.class);
        when(request.getHeader("Accept")).thenReturn(acceptMediaType);
        return request;
    }

    private NativeWebRequest request() {
        return mock(NativeWebRequest.class);
    }

}