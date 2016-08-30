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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.compose;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.RESET_CONTENT;
import static org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM;

public final class SpringAdviceTraitTest {

    private final SpringAdviceTrait unit = new SpringAdviceTrait() {
    };

    @Test
    public void buildsOnThrowable() throws HttpMediaTypeNotAcceptableException {
        HttpStatusAdapter adapter = new HttpStatusAdapter(RESET_CONTENT);

        final ResponseEntity<Problem> result = unit.create(HttpStatus.RESET_CONTENT,
                new IllegalStateException("Message"), mock(NativeWebRequest.class));

        assertThat(result, hasFeature("Status", ResponseEntity::getStatusCode, is(RESET_CONTENT)));
        assertThat(result.getHeaders(), hasFeature("Content-Type", HttpHeaders::getContentType, is(PROBLEM)));
        assertThat(result.getBody(), compose(hasFeature("Status", Problem::getStatus, is(adapter)))
                .and(hasFeature("Detail", Problem::getDetail, is("Message"))));
    }

    @Test
    public void toProblemWithoutCause() {
        final ThrowableProblem problem = unit.toProblem(new IllegalStateException("Message"), BAD_REQUEST);

        assertThat(problem.getCause(), nullValue());
        assertThat(problem.getMessage(),
                allOf(containsString(BAD_REQUEST.getReasonPhrase()), containsString("Message")));
    }
}
