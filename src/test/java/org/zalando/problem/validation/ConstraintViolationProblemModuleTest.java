package org.zalando.problem.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;
import org.zalando.problem.spring.web.advice.validation.Violation;

import static com.jayway.jsonassert.JsonAssert.with;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@Slf4j
public class ConstraintViolationProblemModuleTest {

    @Test
    public void shouldSerialize() throws JsonProcessingException {
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

}