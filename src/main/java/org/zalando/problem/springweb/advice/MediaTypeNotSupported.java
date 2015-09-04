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

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.springweb.EntityBuilder;

import javax.ws.rs.core.Response;
import java.util.List;

@ControllerAdvice
public interface MediaTypeNotSupported {

    @ExceptionHandler
    default ResponseEntity<Problem> handleMediaTypeNotSupportedException(
            final HttpMediaTypeNotSupportedException exception,
            final NativeWebRequest request) {
        return EntityBuilder.buildEntity(Response.Status.UNSUPPORTED_MEDIA_TYPE, exception, request, builder -> {
            final List<MediaType> mediaTypes = exception.getSupportedMediaTypes();

            if (!CollectionUtils.isEmpty(mediaTypes)) {
                final HttpHeaders headers = new HttpHeaders();
                headers.setAccept(mediaTypes);
                return builder.headers(headers);
            }
            return builder;

        });
    }
}
