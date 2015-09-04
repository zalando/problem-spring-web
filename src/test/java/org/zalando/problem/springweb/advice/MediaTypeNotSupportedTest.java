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
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.mockito.Mockito.mock;

public class MediaTypeNotSupportedTest {

    private final MediaTypeNotSupported unit = new MediaTypeNotSupported() {
    };

    @Test
    public void noAcceptHeaderIfNonSupported() {

        final ResponseEntity<Problem> entity = unit.handleMediaTypeNotSupportedException(
                new HttpMediaTypeNotSupportedException("non supported"), mock(NativeWebRequest.class));

        assertThat(entity.getHeaders(), not(hasKey("Accept")));

    }

}
