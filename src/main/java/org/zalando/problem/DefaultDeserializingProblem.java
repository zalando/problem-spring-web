package org.zalando.problem;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response.StatusType;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.ThrowableProblem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DefaultDeserializingProblem extends AbstractThrowableProblem {

	private static final long serialVersionUID = -4006274522829564180L;

	@JsonCreator
	public DefaultDeserializingProblem(@JsonProperty("type") @Nullable final URI type,
			@JsonProperty("title") @Nullable final String title,
			@JsonProperty("status") @Nullable final StatusType status,
			@JsonProperty("detail") @Nullable final String detail,
			@JsonProperty("instance") @Nullable final URI instance,
			@JsonProperty("cause") @Nullable final ThrowableProblem cause,
			@JsonProperty("parameters") @Nullable final Map<String, Object> parameters) {
        super(type, title, status, detail, instance, cause, parameters);
    }
}
