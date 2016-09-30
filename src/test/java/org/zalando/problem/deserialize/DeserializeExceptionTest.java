package org.zalando.problem.deserialize;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.zalando.problem.DefaultDeserializingProblem;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.deserialize.DeserializeException;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;

import com.google.common.io.ByteStreams;

public class DeserializeExceptionTest {

    @Test
	public void deserializeValidationException() throws IOException {
    	byte[] bytes = loadJsonFile("/org/zalando/problem/deserialize/ValidationError.json");
		final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request", bytes, Charset.forName("UTF-8"));
		
		final ThrowableProblem problem = DeserializeException.create().extractException(exception);
		
		Assert.assertTrue(problem instanceof ConstraintViolationProblem);
        Assert.assertEquals(400, problem.getStatus().getStatusCode());
        Assert.assertEquals(4, ((ConstraintViolationProblem) problem).getViolations().size());
	}

    @Test
	public void deserializeUnknownMethodException() throws IOException {
    	byte[] bytes = loadJsonFile("/org/zalando/problem/deserialize/UnknownMethod.json");
		final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", bytes, Charset.forName("UTF-8"));
		
		final ThrowableProblem problem = DeserializeException.create().extractException(exception);
		
		Assert.assertTrue(problem instanceof DefaultDeserializingProblem);
        Assert.assertEquals(405, problem.getStatus().getStatusCode());
        Assert.assertEquals(Family.CLIENT_ERROR, problem.getStatus().getFamily());
        Assert.assertEquals("Method Not Allowed", problem.getStatus().getReasonPhrase());
	}

    @Test
	public void deserializeUnknownStateException() throws IOException {
    	byte[] bytes = loadJsonFile("/org/zalando/problem/deserialize/UnknownState.json");
		final HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", bytes, Charset.forName("UTF-8"));
		
		final ThrowableProblem problem = DeserializeException.create().extractException(exception);
		
		Assert.assertTrue(problem instanceof DefaultDeserializingProblem);
        Assert.assertNull(problem.getStatus());
	}

	private byte[] loadJsonFile(final String fileLocation) throws IOException {
		final InputStream inputStream = this.getClass().getResourceAsStream(fileLocation);
    	Assert.assertNotNull("Unable to find the json file with test data", inputStream);
    	return ByteStreams.toByteArray(inputStream);
	}
}
