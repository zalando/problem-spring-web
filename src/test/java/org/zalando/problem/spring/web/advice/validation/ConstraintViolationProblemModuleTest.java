package org.zalando.problem.spring.web.advice.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

import java.net.URI;

import static com.jayway.jsonassert.JsonAssert.with;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@Slf4j
final class ConstraintViolationProblemModuleTest {

    @Test
    void shouldSerializeWithoutAutoDetect() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper()
                .disable(MapperFeature.AUTO_DETECT_FIELDS)
                .disable(MapperFeature.AUTO_DETECT_GETTERS)
                .disable(MapperFeature.AUTO_DETECT_IS_GETTERS)
                .registerModule(new ProblemModule())
                .registerModule(new ConstraintViolationProblemModule());

        final Violation violation = new Violation("bob", "was missing");
        final ConstraintViolationProblem unit = new ConstraintViolationProblem(BAD_REQUEST, singletonList(violation));

        with(mapper.writeValueAsString(unit))
                .assertThat("status", is(400))
                .assertThat("type", is(ConstraintViolationProblem.TYPE_VALUE))
                .assertThat("title", is("Constraint Violation"))
                .assertThat("violations", hasSize(1))
                .assertThat("violations.*.field", contains("bob"))
                .assertThat("violations.*.message", contains("was missing"));
    }

    @Test
    void shouldSerializeCustomType() throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ProblemModule())
                .registerModule(new ConstraintViolationProblemModule());

        final URI type = URI.create("foo");
        final ConstraintViolationProblem unit = new ConstraintViolationProblem(type, BAD_REQUEST, emptyList());

        with(mapper.writeValueAsString(unit))
                .assertThat("type", is("foo"));
    }

}
