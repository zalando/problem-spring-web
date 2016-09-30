package org.zalando.problem.spring.web.advice.deserialize;

import java.io.IOException;

import javax.ws.rs.core.Response.StatusType;

import org.springframework.web.client.HttpClientErrorException;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.AbstractPolymorphicThrowableProblem;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DeserializeException {

    public static ThrowableProblem extractException(final HttpClientErrorException exception) throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper mapper = new ObjectMapper();
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(StatusType.class, new StatusDeserializer());

        mapper.addMixIn(ThrowableProblem.class, AbstractPolymorphicThrowableProblem.class);
        mapper.registerModule(module);
        mapper.setSerializationInclusion(Include.NON_NULL);

        final String responseBody = exception.getResponseBodyAsString();
        final ThrowableProblem problem = mapper.readValue(responseBody, ThrowableProblem.class);
        return problem;
    }
}
