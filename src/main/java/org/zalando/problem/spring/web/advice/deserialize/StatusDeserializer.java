package org.zalando.problem.spring.web.advice.deserialize;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class StatusDeserializer extends StdDeserializer<StatusType> {

	private static final long serialVersionUID = 6962438048936188232L;

	public class SimpleStatusType implements StatusType {

		private final int statusCode;
		private final Family family;
		private final String reasonPhrase;

		@JsonCreator
		public SimpleStatusType(@JsonProperty("statusCode") final int pStatusCode, @JsonProperty("family") final Family pFamily,
				@JsonProperty("reasonPhrase") final String pReasonPhrase) {
			super();
			this.statusCode = pStatusCode;
			this.family = pFamily;
			this.reasonPhrase = pReasonPhrase;
		}

		@Override
		public int getStatusCode() {
			return statusCode;
		}

		@Override
		public Family getFamily() {
			return family;
		}

		@Override
		public String getReasonPhrase() {
			return reasonPhrase;
		}
	}

	public StatusDeserializer() { 
        this(null); 
    }

    public StatusDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public StatusType deserialize(final JsonParser pJasonParser, final DeserializationContext pCtxt)
			throws IOException, JsonProcessingException {
        final JsonNode node = pJasonParser.getCodec().readTree(pJasonParser);
        if (node.isTextual()) {
        	final Status statusEnum = Status.valueOf(node.asText());
        	return new SimpleStatusType(statusEnum.getStatusCode(), statusEnum.getFamily(), statusEnum.getReasonPhrase());
        }
        return null;
	}
}
