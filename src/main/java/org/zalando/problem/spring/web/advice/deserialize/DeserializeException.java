package org.zalando.problem.spring.web.advice.deserialize;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.web.client.HttpClientErrorException;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.AbstractPolymorphicThrowableProblem;

import javax.ws.rs.core.Response.StatusType;

import java.io.IOException;

public final class DeserializeException {

  private static volatile DeserializeException deserializeException = null;

  public static DeserializeException instance() {
    if (DeserializeException.deserializeException == null) {
      synchronized (DeserializeException.class) {
        DeserializeException.deserializeException = new DeserializeException();
      }
    }
    return DeserializeException.deserializeException;
  }

  private DeserializeException() {
    super();
  }

  public ThrowableProblem extractException(final String serializedException)
      throws JsonParseException, JsonMappingException, IOException {
    final ObjectMapper mapper = new ObjectMapper();
    final SimpleModule module = new SimpleModule();
    module.addDeserializer(StatusType.class, new StatusDeserializer());

    mapper.addMixIn(ThrowableProblem.class, AbstractPolymorphicThrowableProblem.class);
    mapper.registerModule(module);
    mapper.setSerializationInclusion(Include.NON_NULL);

    final ThrowableProblem problem = mapper.readValue(serializedException, ThrowableProblem.class);
    return problem;
  }

  public ThrowableProblem extractException(final HttpClientErrorException exception)
      throws JsonParseException, JsonMappingException, IOException {
    return this.extractException(exception.getResponseBodyAsString());
  }
}
