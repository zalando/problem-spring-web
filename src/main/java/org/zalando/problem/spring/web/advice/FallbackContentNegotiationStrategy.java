package org.zalando.problem.spring.web.advice;

import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Mimics the new default behavior as of Spring 5 that
 * {@link ContentNegotiationStrategy#resolveMediaTypes(NativeWebRequest)} returns {@link MediaType#ALL} as a fallback
 * compared to an empty list.
 */
final class FallbackContentNegotiationStrategy implements ContentNegotiationStrategy {

    private final List<MediaType> all = Collections.singletonList(MediaType.ALL);
    private final ContentNegotiationStrategy delegate;

    FallbackContentNegotiationStrategy(final ContentNegotiationStrategy delegate) {
        this.delegate = delegate;
    }

    @Override
    @Nonnull
    public List<MediaType> resolveMediaTypes(final NativeWebRequest request)
            throws HttpMediaTypeNotAcceptableException {
        final List<MediaType> mediaTypes = delegate.resolveMediaTypes(request);

        if (mediaTypes.isEmpty()) {
            return all;
        }

        return mediaTypes;
    }

}
