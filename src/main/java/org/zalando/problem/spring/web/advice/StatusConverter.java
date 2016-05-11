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

import com.google.common.collect.ImmutableMap;
import org.springframework.http.HttpStatus;
import org.zalando.problem.MoreStatus;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response;

import java.util.Arrays;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;
import static javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.StatusType;

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
 * For unmappable {@link HttpStatus} an instance of {@link UnknownStatus} will be created.
 *
 * @see HttpStatus
 * @see StatusType
 * @see Response.Status
 * @see MoreStatus
 * @see UnknownStatus
 */
public interface StatusConverter {

    ImmutableMap<Integer, Response.StatusType> INDEX = Arrays.asList(Status.class, MoreStatus.class)
            .stream()
            .map(Class::getEnumConstants)
            // voluntary unused of static method Arrays::stream to avoid compilation error with jdk1.8.0_31.
            // Indeed (at least) jdk1.8.0_31 has regression on type inference, we must force type casting as workaround
            .flatMap(statuses -> Arrays.stream((StatusType[]) statuses))
            .collect(collectingAndThen(toMap(StatusType::getStatusCode, identity()), ImmutableMap::copyOf));

    default StatusType convert(final HttpStatus httpStatus) {
        final int statusCode = httpStatus.value();
        @Nullable final StatusType statusType = INDEX.get(statusCode);
        return statusType == null ? new UnknownStatus(statusCode) : statusType;
    }

}
