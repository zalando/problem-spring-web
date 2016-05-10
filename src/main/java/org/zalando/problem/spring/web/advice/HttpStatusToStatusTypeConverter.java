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

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.zalando.problem.MoreStatus;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import java.util.Arrays;

/**
 * Converts from Spring {@link HttpStatus} to Jax-RS {@link StatusType}.
 * <p>
 * Converter only takes care of status code, thus if there is multiple {@link HttpStatus} for same status code like:
 * <ul>
 *  <li>{@link HttpStatus#MOVED_TEMPORARILY}</li>
 *  <li>{@link HttpStatus#FOUND}</li>
 * </ul>
 * Reason phrase will be depend on {@link StatusType} implementation.
 * <p>
 * Example: {@link HttpStatus#MOVED_TEMPORARILY} will be converted to {@link Response.Status#FOUND}.
 * <p>
 * For unmappable {@link HttpStatus} converter will throw an {@link IllegalArgumentException}.
 *
 * @see HttpStatus
 * @see StatusType
 * @see Response.Status
 * @see MoreStatus
 */
final class HttpStatusToStatusTypeConverter implements Converter<HttpStatus, StatusType> {

    /**
     * @throws IllegalArgumentException for unmappable {@code httpStatus}.
     */
    @Override
    public StatusType convert(HttpStatus httpStatus) {
        StatusType statusType;
        statusType = Response.Status.fromStatusCode(httpStatus.value());
        if (statusType == null) {
            statusType = Arrays.stream(MoreStatus.values())
                               .filter(s -> s.getStatusCode() == httpStatus.value())
                               .findFirst()
                               .orElseThrow(() -> {
                                   return new IllegalArgumentException(
                                           "Unable to convert " + HttpStatus.class.getName() + " \""
                                           + httpStatus.getReasonPhrase() + "\" to " + StatusType.class.getName());
                               });
        }
        return statusType;
    }
}
