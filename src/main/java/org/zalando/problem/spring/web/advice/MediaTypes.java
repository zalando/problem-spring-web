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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

final class MediaTypes {

    static final String PROBLEM_VALUE = "application/problem+json";
    static final MediaType PROBLEM = MediaType.parseMediaType(PROBLEM_VALUE);

    static final String X_PROBLEM_VALUE = "application/x.problem+json";
    static final MediaType X_PROBLEM = MediaType.parseMediaType(X_PROBLEM_VALUE);

    static final String WILDCARD_JSON_VALUE = "application/*+json";
    static final MediaType WILDCARD_JSON = MediaType.parseMediaType(WILDCARD_JSON_VALUE);

    private static final Logger LOG = LoggerFactory.getLogger(MediaTypes.class);

    private static final HeaderContentNegotiationStrategy headerNegotiator = new HeaderContentNegotiationStrategy();

    MediaTypes() {
        // package private so we can trick code coverage
    }

    static Optional<MediaType> determineContentType(final NativeWebRequest request) {
        try {
            final List<MediaType> acceptedMediaTypes = headerNegotiator.resolveMediaTypes(request);

            if (acceptedMediaTypes.isEmpty()) {
                return Optional.of(PROBLEM);
            }

            if (acceptedMediaTypes.stream().anyMatch(PROBLEM::isCompatibleWith)) {
                return Optional.of(PROBLEM);
            }

            if (acceptedMediaTypes.stream().anyMatch(X_PROBLEM::isCompatibleWith)) {
                return Optional.of(X_PROBLEM);
            }

            if (acceptedMediaTypes.stream().anyMatch(WILDCARD_JSON::isCompatibleWith)) {
                return Optional.of(PROBLEM);
            }

            if (acceptedMediaTypes.stream().anyMatch(APPLICATION_JSON::isCompatibleWith)) {
                return Optional.of(PROBLEM);
            }

        } catch (final HttpMediaTypeNotAcceptableException exception) {
            LOG.info("Unable to determine content type due to error during parsing Accept header: [{}]", exception);
        }
        return Optional.empty();
    }
}
