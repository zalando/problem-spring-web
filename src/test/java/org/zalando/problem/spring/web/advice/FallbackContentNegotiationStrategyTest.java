package org.zalando.problem.spring.web.advice;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.ALL;
import static org.springframework.http.MediaType.APPLICATION_JSON;

final class FallbackContentNegotiationStrategyTest {

    private final ContentNegotiationStrategy delegate = mock(ContentNegotiationStrategy.class);
    private final ContentNegotiationStrategy unit = new FallbackContentNegotiationStrategy(delegate);

    @Test
    void shouldPassThroughResolvedMediaTypes() throws HttpMediaTypeNotAcceptableException {
        final List<MediaType> json = singletonList(APPLICATION_JSON);
        when(delegate.resolveMediaTypes(any())).thenReturn(json);

        final List<MediaType> actual = unit.resolveMediaTypes(mock(NativeWebRequest.class));

        assertSame(json, actual);
    }

    @Test
    void shouldFallback() throws HttpMediaTypeNotAcceptableException {
        when(delegate.resolveMediaTypes(any())).thenReturn(emptyList());

        final List<MediaType> actual = unit.resolveMediaTypes(mock(NativeWebRequest.class));

        assertEquals(singletonList(ALL), actual);
    }

}
