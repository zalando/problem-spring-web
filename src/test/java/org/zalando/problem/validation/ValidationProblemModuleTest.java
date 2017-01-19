package org.zalando.problem.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonassert.JsonAssert;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;
import org.zalando.problem.spring.web.advice.validation.Violation;

import javax.ws.rs.core.Response;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@Slf4j
public class ValidationProblemModuleTest {

    @Test
    public void serializeConstraintViolationUsingMixinAnnotations() throws JsonProcessingException {
        ObjectMapper mapperWithDisabledAutoDetection = new ObjectMapper()
                .configure(MapperFeature.AUTO_DETECT_FIELDS, false)
                .configure(MapperFeature.AUTO_DETECT_GETTERS, false)
                .configure(MapperFeature.AUTO_DETECT_IS_GETTERS, false);

        mapperWithDisabledAutoDetection.registerModule(new ProblemModule());
        mapperWithDisabledAutoDetection.registerModule(new ValidationProblemModule());

        Violation violation = new Violation("bob", "was missing");
        ConstraintViolationProblem constraintViolationProblem = new ConstraintViolationProblem(Response.Status.BAD_REQUEST, asList(violation));


        String json = mapperWithDisabledAutoDetection.writeValueAsString(constraintViolationProblem);

        JsonAssert.with(json)
                .assertThat("status", is(400))
                .assertThat("type", is(ConstraintViolationProblem.TYPE_VALUE))
                .assertThat("title", is("Constraint Violation"))
                .assertThat("violations", hasSize(1))
                .assertThat("violations.*.field", contains("bob"))
                .assertThat("violations.*.message", contains("was missing"));
    }

}