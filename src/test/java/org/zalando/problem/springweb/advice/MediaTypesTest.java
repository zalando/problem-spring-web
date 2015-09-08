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
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.hobsoft.hamcrest.compose.ComposeMatchers.hasFeature;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_ATOM_XML;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.zalando.problem.springweb.advice.MediaTypes.PROBLEM;
import static org.zalando.problem.springweb.advice.MediaTypes.WILDCARD_JSON;
import static org.zalando.problem.springweb.advice.MediaTypes.X_PROBLEM;
import static org.zalando.problem.springweb.advice.MediaTypes.determineContentType;

public class MediaTypesTest {

    @Test
    public void isApplicationProblemJson() {
        assertThat(PROBLEM, hasFeature("Type", MediaType::getType, is("application")));
        assertThat(PROBLEM, hasFeature("Subtype", MediaType::getSubtype, is("problem+json")));
        assertThat(PROBLEM, hasFeature("Parameters", MediaType::getParameters, is(aMapWithSize(0))));
    }

    @Test
    public void isApplicationXProblemJson() {
        assertThat(X_PROBLEM, hasFeature("Type", MediaType::getType, is("application")));
        assertThat(X_PROBLEM, hasFeature("Subtype", MediaType::getSubtype, is("x.problem+json")));
        assertThat(X_PROBLEM, hasFeature("Parameters", MediaType::getParameters, is(aMapWithSize(0))));
    }

    @Test
    public void areCompatibleWithApplicationJsonWildcard() {
        assertThat(PROBLEM.isCompatibleWith(MediaType.parseMediaType("application/*+json")), is(true));
        assertThat(X_PROBLEM.isCompatibleWith(MediaType.parseMediaType("application/*+json")), is(true));
    }

    @Test
    public void problemGivesProblem() {
        final Optional<MediaType> contentType = determineContentType(request(PROBLEM));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(PROBLEM));
    }

    @Test
    public void xproblemGivesXProblem() {
        final Optional<MediaType> contentType = determineContentType(request(X_PROBLEM));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(X_PROBLEM));
    }

    @Test
    public void jsonGivesProblem() {
        final Optional<MediaType> contentType = determineContentType(request(APPLICATION_JSON));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(PROBLEM));
    }

    @Test
    public void wildcardJsonGivesProblem() {
        final Optional<MediaType> contentType = determineContentType(request(WILDCARD_JSON));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(PROBLEM));
    }

    @Test
    public void specificJsonGivesProblem() {
        final MediaType customMediaType = MediaType.parseMediaType("application/x.vendor.specific+json");
        final Optional<MediaType> contentType = determineContentType(request(customMediaType));
        assertThat(contentType, is(not(Optional.empty())));
        assertThat(contentType.get(), is(PROBLEM));
    }

    @Test
    public void nonJsonGivesEmpty() {
        final Optional<MediaType> contentType = determineContentType(request(APPLICATION_ATOM_XML));
        assertThat(contentType, is(Optional.empty()));
    }

    @Test
    public void invalidGivesEmpty() {
        final Optional<MediaType> contentType = determineContentType(request("_|°|_"));
        assertThat(contentType, is(Optional.empty()));
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
