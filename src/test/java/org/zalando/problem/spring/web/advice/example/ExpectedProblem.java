package org.zalando.problem.spring.web.advice.example;

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


import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;

public class ExpectedProblem extends ThrowableProblem {

    private final String detail;

    public ExpectedProblem(final String detail) {
        this.detail = detail;
    }

    @Override
    public URI getType() {
        return URI.create("about:blank");
    }

    @Override
    public String getTitle() {
        return "Expected";
    }

    @Override
    public Response.StatusType getStatus() {
        return Status.CONFLICT;
    }

    @Override
    public String getDetail() {
        return detail;
    }

}
