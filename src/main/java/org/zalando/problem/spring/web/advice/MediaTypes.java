package org.zalando.problem.spring.web.advice;

import org.apiguardian.api.API;
import org.springframework.http.MediaType;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public final class MediaTypes {

    public static final String PROBLEM_VALUE = "application/problem+json";
    public static final MediaType PROBLEM = MediaType.parseMediaType(PROBLEM_VALUE);

    static final String X_PROBLEM_VALUE = "application/x.problem+json";
    static final MediaType X_PROBLEM = MediaType.parseMediaType(X_PROBLEM_VALUE);

    @Deprecated
    static final String WILDCARD_JSON_VALUE = "application/*+json";

    @Deprecated
    static final MediaType WILDCARD_JSON = MediaType.parseMediaType(WILDCARD_JSON_VALUE);

    MediaTypes() {
        // package private so we can trick code coverage
    }

}
