package org.zalando.problem.spring.web.advice;


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
