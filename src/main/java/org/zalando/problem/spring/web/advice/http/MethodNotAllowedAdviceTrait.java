package org.zalando.problem.spring.web.advice.http;

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

import com.google.gag.annotation.remark.Facepalm;
import com.google.gag.annotation.remark.WTF;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response.Status;

public interface MethodNotAllowedAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException exception,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {

        @WTF
        @Facepalm("Nullable arrays... great work from Spring :/")
        @Nullable final String[] methods = exception.getSupportedMethods();

        if (methods == null || methods.length == 0) {
            return create(Status.METHOD_NOT_ALLOWED, exception, request);
        }
        
        final HttpHeaders headers = new HttpHeaders();
        headers.setAllow(exception.getSupportedHttpMethods());

        return create(Status.METHOD_NOT_ALLOWED, exception, request, headers);
    }

}
