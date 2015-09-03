package org.zalando.problem.springweb;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;

import javax.ws.rs.core.Response;
import java.util.Optional;

import static org.zalando.problem.springweb.MediaTypes.determineContentType;
import static org.zalando.problem.springweb.StatusMapper.map;

public interface EntityBuilder {

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
        final Optional<MediaType> contentType = determineContentType(request);
        if (contentType.isPresent()) {
            return builder.contentType(contentType.get()).body(problem);
        }
        return builder.body(null);
    }

}
