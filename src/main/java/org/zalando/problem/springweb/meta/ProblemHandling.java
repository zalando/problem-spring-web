package org.zalando.problem.springweb.meta;

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

import org.zalando.problem.springweb.advice.MediaTypeNotAcceptable;
import org.zalando.problem.springweb.advice.MediaTypeNotSupported;
import org.zalando.problem.springweb.advice.MessageNotReadable;
import org.zalando.problem.springweb.advice.MethodArgumentNotValid;
import org.zalando.problem.springweb.advice.MissingServletRequestParameter;
import org.zalando.problem.springweb.advice.MissingServletRequestPart;
import org.zalando.problem.springweb.advice.Multipart;
import org.zalando.problem.springweb.advice.NoSuchRequestHandlingMethod;
import org.zalando.problem.springweb.advice.ProblemTrait;
import org.zalando.problem.springweb.advice.RequestMethodNotSupported;
import org.zalando.problem.springweb.advice.ServletRequestBinding;
import org.zalando.problem.springweb.advice.ThrowableTrait;
import org.zalando.problem.springweb.advice.TypeMistmatch;

public interface ProblemHandling extends
        MediaTypeNotAcceptable,
        MediaTypeNotSupported,
        MessageNotReadable,
        MethodArgumentNotValid,
        MissingServletRequestParameter,
        MissingServletRequestPart,
        Multipart,
        NoSuchRequestHandlingMethod,
        ProblemTrait,
        RequestMethodNotSupported,
        ServletRequestBinding,
        ThrowableTrait,
        TypeMistmatch {
}
