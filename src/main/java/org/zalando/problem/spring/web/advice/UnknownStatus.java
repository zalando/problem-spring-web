package org.zalando.problem.spring.web.advice;

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

import javax.annotation.concurrent.Immutable;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

@Immutable
final class UnknownStatus implements Response.StatusType {

    private final int statusCode;

    UnknownStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public Family getFamily() {
        return Family.OTHER;
    }

    @Override
    public String getReasonPhrase() {
        return "Unknown";
    }

}
