package org.zalando.problem.springweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.zalando.problem.springweb.MediaTypes.WILDCARD_JSON;
import static org.zalando.problem.springweb.StatusMapper.map;

public interface EntityBuilder {

    Logger LOG = LoggerFactory.getLogger(EntityBuilder.class);

    HeaderContentNegotiationStrategy headerNegotiator = new HeaderContentNegotiationStrategy();

    static ResponseEntity<Problem> buildEntity(final Response.Status status, final Throwable throwable,
                                               final NativeWebRequest request) {
        return buildEntity(status, throwable.getMessage(), request);
    }

    static ResponseEntity<Problem> buildEntity(final Response.Status status, final String message,
                                               final NativeWebRequest request) {
        return buildEntity(Problem.valueOf(status, message), request);
    }

    static ResponseEntity<Problem> buildEntity(final Problem problem, final NativeWebRequest request) {
        final ResponseEntity.BodyBuilder builder = ResponseEntity.status(map(problem.getStatus()));
        if (isCompatibleWithProblem(request)) {
            return builder
                    .contentType(MediaTypes.PROBLEM)
                    .body(problem);
        }
        return builder.body(null);
    }

    static boolean isCompatibleWithProblem(final NativeWebRequest request) {
        try {
            final List<MediaType> acceptedMediaTypes = headerNegotiator.resolveMediaTypes(request);
            if (acceptedMediaTypes.isEmpty() || acceptedMediaTypes.stream().anyMatch(WILDCARD_JSON::includes)) {
                return true;
            }
        } catch (final HttpMediaTypeNotAcceptableException exception) {
            LOG.info("Falling back to empty body due to error during parsing Accept header: [{}]", exception);
        }
        return false;
    }

}
