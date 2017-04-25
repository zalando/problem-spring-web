package org.zalando.problem.spring.web.advice.deserialize;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.springframework.web.client.HttpClientErrorException;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;

import javax.ws.rs.core.Response.StatusType;

import java.io.IOException;

public final class DeserializeException {

  private static volatile DeserializeException deserializeException = null;
  private final ObjectMapper mapper;

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
    this.mapper = new ObjectMapper();
    this.mapper.registerModule(new ProblemModule().withStackTraces());
    final SimpleModule module = new SimpleModule();
    module.addDeserializer(StatusType.class, new StatusDeserializer());
    module.registerSubtypes(ConstraintViolationProblem.class);
    this.mapper.registerModule(module);
  }

  public ThrowableProblem extractException(final String serializedException)
      throws JsonParseException, JsonMappingException, IOException {
    return this.mapper.readValue(serializedException, ThrowableProblem.class);
  }

  public ThrowableProblem extractException(final HttpClientErrorException exception)
      throws JsonParseException, JsonMappingException, IOException {
    return this.extractException(exception.getResponseBodyAsString());
  }
}
