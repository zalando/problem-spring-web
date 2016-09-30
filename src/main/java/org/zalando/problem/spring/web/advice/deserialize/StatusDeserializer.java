package org.zalando.problem.spring.web.advice.deserialize;

import java.io.IOException;

import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

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
        if (node == null) {
        	return null;
        }
        if (node.isTextual()) {
        	final Status statusEnum = Status.valueOf(node.asText());
        	return new SimpleStatusType(statusEnum.getStatusCode(), statusEnum.getFamily(), statusEnum.getReasonPhrase());
        }
        final int statusCode =  node.has("statusCode") ? (Integer) ((IntNode) node.get("statusCode")).numberValue() : 0;
        final Family family = node.has("family") ? Family.valueOf(node.get("family").asText()) : null;
        final String reasonPhrase = node.has("reasonPhrase") ? node.get("reasonPhrase").asText() : null;
 
        return new SimpleStatusType(statusCode, family, reasonPhrase);
	}

}
