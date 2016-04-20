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


import org.springframework.http.MediaType;

final class MediaTypes {

    static final String PROBLEM_VALUE = "application/problem+json";
    static final MediaType PROBLEM = MediaType.parseMediaType(PROBLEM_VALUE);

    static final String X_PROBLEM_VALUE = "application/x.problem+json";
    static final MediaType X_PROBLEM = MediaType.parseMediaType(X_PROBLEM_VALUE);

    static final String WILDCARD_JSON_VALUE = "application/*+json";
    static final MediaType WILDCARD_JSON = MediaType.parseMediaType(WILDCARD_JSON_VALUE);

    MediaTypes() {
        // package private so we can trick code coverage
    }

}
