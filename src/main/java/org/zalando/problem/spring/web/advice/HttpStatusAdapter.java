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

import com.google.common.base.Objects;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;

/**
 * An implementation of {@link javax.ws.rs.core.Response.StatusType} to map {@link HttpStatus}.
 */
class HttpStatusAdapter implements Response.StatusType {

    private final HttpStatus status;

    HttpStatusAdapter(HttpStatus status) {
        this.status = status;
    }

    @Override
    public int getStatusCode() {
        return status.value();
    }

    @Override
    public Family getFamily() {
        return Family.familyOf(status.value());
    }

    @Override
    public String getReasonPhrase() {
        return status.getReasonPhrase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpStatusAdapter)) {
            return false;
        }
        HttpStatusAdapter that = (HttpStatusAdapter) o;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(status);
    }
}
