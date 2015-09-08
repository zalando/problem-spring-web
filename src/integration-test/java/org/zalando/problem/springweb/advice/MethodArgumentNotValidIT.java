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


import org.junit.Test;
import org.springframework.web.bind.annotation.ControllerAdvice;

public class MethodArgumentNotValidIT extends AdviceIT {

    @ControllerAdvice
    private static class Advice implements MethodArgumentNotValid {
    }

    @Override
    protected MethodArgumentNotValid advice() {
        return new Advice();
    }

    @Test
    public void missingRequestBody() throws Exception {
        // TODO: method argument not valid test
    }

}