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

import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.Response.StatusType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.*;

public final class HttpStatusToStatusTypeConverterTest {

    private static final Converter<HttpStatus, StatusType> CONVERTER = new HttpStatusToStatusTypeConverter();

    private static final Set<HttpStatus> UNSUPPORTED_STATUSES = new HashSet<HttpStatus>() {{
        add(INSUFFICIENT_SPACE_ON_RESOURCE);
        add(METHOD_FAILURE);
        add(DESTINATION_LOCKED);
    }};

    @Test
    public void shouldConvertHttpStatusToStatusType() {
        HttpStatus[] httpStatuses = HttpStatus.values();
        Arrays.stream(httpStatuses)
              .filter(s -> !UNSUPPORTED_STATUSES.contains(s))
              .forEach(s -> {
                  StatusType statusType = CONVERTER.convert(s);
                  assertThat(statusType.getStatusCode(), is(s.value()));
              });
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfUnableToConvert() {
        CONVERTER.convert(UNSUPPORTED_STATUSES.iterator().next());
    }

    @Test
    public void shouldAlwaysPreferStatusTypeReasonPhrase() {
        StatusType statusType = CONVERTER.convert(MOVED_TEMPORARILY);
        assertThat(statusType.getReasonPhrase(), not(MOVED_PERMANENTLY.getReasonPhrase()));
    }
}
