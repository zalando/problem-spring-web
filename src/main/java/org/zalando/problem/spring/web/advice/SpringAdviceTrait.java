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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.Response.StatusType;

/**
 * Spring version of {@link AdviceTrait} which uses {@link HttpStatus} rather than
 * {@link StatusType}.
 *
 * @see AdviceTrait
 */
public interface SpringAdviceTrait extends AdviceTrait {

    default ResponseEntity<Problem> create(final HttpStatus status, final Throwable throwable,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return create(status, throwable, request, new HttpHeaders());
    }

    default ResponseEntity<Problem> create(final HttpStatus status, final Throwable throwable,
            final NativeWebRequest request, final HttpHeaders headers)
            throws HttpMediaTypeNotAcceptableException {
        return create(toStatus(status), throwable, request, headers);
    }

    default ThrowableProblem toProblem(final Throwable throwable, final HttpStatus status) {
        return toProblem(throwable, toStatus(status));
    }

    default StatusType toStatus(HttpStatus status) {
        return new HttpStatusAdapter(status);
    }

}
