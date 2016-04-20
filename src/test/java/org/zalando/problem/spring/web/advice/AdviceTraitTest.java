package org.zalando.problem.spring.web.advice;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.compose;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.RESET_CONTENT;
import static org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM;
import static org.zalando.problem.spring.web.advice.MediaTypes.WILDCARD_JSON_VALUE;

public class AdviceTraitTest {
    
    private final AdviceTrait unit = new AdviceTrait() {
    };

    @Test
    public void buildsOnProblem() throws HttpMediaTypeNotAcceptableException {
        final ThrowableProblem problem = mock(ThrowableProblem.class);
        when(problem.getStatus()).thenReturn(Status.RESET_CONTENT);

        final ResponseEntity<Problem> result = unit.create(problem, request());

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)));
    }

    @Test
    public void buildsOnThrowable() throws HttpMediaTypeNotAcceptableException {
        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT, 
                new IllegalStateException("Message"), request());

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is(Optional.of("Message")))));
    }

    @Test
    public void buildsOnMessage() throws HttpMediaTypeNotAcceptableException {
        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT, 
                new IllegalStateException("Message"), request());

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is(Optional.of("Message")))));
    }

    @Test
    public void buildsIfIncludes() throws HttpMediaTypeNotAcceptableException {
        final String message = "Message";

        final ResponseEntity<Problem> result = unit.create(Status.RESET_CONTENT,
                new IllegalStateException(message),
                request(WILDCARD_JSON_VALUE));

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(Status.RESET_CONTENT)))
                .and(hasFeature("Detail", Problem::getDetail, is(Optional.of(message)))));
    }

    @Test
    public void mapsStatus() throws HttpMediaTypeNotAcceptableException {
        final HttpStatus expected = HttpStatus.BAD_REQUEST;
        final Response.StatusType input = Status.BAD_REQUEST;
        final ResponseEntity<Problem> entity = unit.create(input, 
                new IllegalStateException("Checkpoint"), request());

        assertThat(entity.getStatusCode(), is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsOnUnknownStatus() throws HttpMediaTypeNotAcceptableException {
        final Response.StatusType input = mock(Response.StatusType.class);
        when(input.getReasonPhrase()).thenReturn("L33t");
        when(input.getStatusCode()).thenReturn(1337);

        unit.create(input, new IllegalStateException("L33t"), request());
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