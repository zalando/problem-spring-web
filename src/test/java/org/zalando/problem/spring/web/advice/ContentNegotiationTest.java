package org.zalando.problem.spring.web.advice;

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
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_ATOM_XML;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.spring.web.advice.MediaTypes.PROBLEM;
import static org.zalando.problem.spring.web.advice.MediaTypes.WILDCARD_JSON;
import static org.zalando.problem.spring.web.advice.MediaTypes.X_PROBLEM;

public final class ContentNegotiationTest {

    private final AdviceTrait unit = new AdviceTrait() {
    };

    @Test
    public void problemGivesProblem() throws HttpMediaTypeNotAcceptableException {
        final Optional<MediaType> contentType = unit.negotiate(request(PROBLEM));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(PROBLEM));
    }

    @Test
    public void xproblemGivesXProblem() throws HttpMediaTypeNotAcceptableException {
        final Optional<MediaType> contentType = unit.negotiate(request(X_PROBLEM));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(X_PROBLEM));
    }

    @Test
    public void jsonGivesProblem() throws HttpMediaTypeNotAcceptableException {
        final Optional<MediaType> contentType = unit.negotiate(request(APPLICATION_JSON));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(PROBLEM));
    }

    @Test
    public void wildcardJsonGivesProblem() throws HttpMediaTypeNotAcceptableException {
        final Optional<MediaType> contentType = unit.negotiate(request(WILDCARD_JSON));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(PROBLEM));
    }

    @Test
    public void specificJsonGivesProblem() throws HttpMediaTypeNotAcceptableException {
        final MediaType customMediaType = MediaType.parseMediaType("application/x.vendor.specific+json");
        final Optional<MediaType> contentType = unit.negotiate(request(customMediaType));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(PROBLEM));
    }

    @Test
    public void nonJsonGivesEmpty() throws HttpMediaTypeNotAcceptableException {
        final Optional<MediaType> contentType = unit.negotiate(request(APPLICATION_ATOM_XML));
        assertThat(contentType, is(Optional.empty()));
    }

    @Test(expected = HttpMediaTypeNotAcceptableException.class)
    public void invalidAcceptHeaderThrows() throws HttpMediaTypeNotAcceptableException {
        unit.negotiate(request("_|°|_"));
    }

    private NativeWebRequest request(final String acceptMediaType) {
        final NativeWebRequest request = mock(NativeWebRequest.class);
        when(request.getHeader("Accept")).thenReturn(acceptMediaType);
        return request;
    }

    private NativeWebRequest request(MediaType acceptMediaType) {
        return request(acceptMediaType.toString());
    }

}
