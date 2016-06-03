package org.zalando.problem.spring.web.advice.routing;

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

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import javax.ws.rs.core.Response.Status;

/**
 * Transforms {@link NoHandlerFoundException NoHandlerFoundExceptions} into {@link Status#NOT_FOUND not-found}
 * {@link Problem problems}.
 * <p>
 * <p>
 * <strong>Note</strong>: This requires {@link DispatcherServlet#setThrowExceptionIfNoHandlerFound(boolean)} being set
 * to true.
 * </p>
 *
 * @see NoHandlerFoundException
 * @see Status#NOT_FOUND
 * @see DispatcherServlet#setThrowExceptionIfNoHandlerFound(boolean)
 */
public interface NoHandlerFoundAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleNoHandlerFound(
            final NoHandlerFoundException exception,
            final NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
        return create(Status.NOT_FOUND, exception, request);
    }

}
