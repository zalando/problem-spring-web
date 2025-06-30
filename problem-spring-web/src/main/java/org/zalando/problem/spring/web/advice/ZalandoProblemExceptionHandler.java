package org.zalando.problem.spring.web.advice;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationAdviceTrait;
import org.zalando.problem.spring.web.advice.validation.ValidationAdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import jakarta.validation.ConstraintViolationException;

/**
 * A controller advice that integrates with Spring's Problem JSON support and
 * also overwrites functionality where Zalando Problem JSON does better handling
 * especially when failing with validation errors.
 */
@ControllerAdvice
public class ZalandoProblemExceptionHandler extends ResponseEntityExceptionHandler {

	private ValidationAdviceTrait validationTrait = new ValidationAdviceTrait() {
	};

	private ConstraintViolationAdviceTrait cveTrait = new ConstraintViolationAdviceTrait() {
	};

	/**
	 * Overridden to add violations to the problem details.
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		List<Violation> violations = validationTrait.createViolations(ex.getBindingResult());

		ProblemDetail body = ex.getBody();
		body.setType(ConstraintViolationProblem.TYPE);
		body.setTitle("Constraint Violation");
		body.setProperty("violations", violations);

		return handleExceptionInternal(ex, body, headers, status, request);
	}

	/**
	 * Overridden to add violations to the problem details.
	 */
	@Override
	@Deprecated(since = "6.0", forRemoval = true)
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatusCode status,
			WebRequest request) {
		List<Violation> violations = validationTrait.createViolations(ex.getBindingResult());

		ProblemDetail body = ProblemDetail.forStatusAndDetail(status, "Failed to bind request");
		body.setType(ConstraintViolationProblem.TYPE);
		body.setTitle("Constraint Violation");
		body.setProperty("violations", violations);

		return handleExceptionInternal(ex, body, headers, status, request);
	}

	/**
	 * Handles Spring's MultipartException.
	 */
	@ExceptionHandler
	public ResponseEntity<Object> handleValidationException(MultipartException ex, WebRequest request)
			throws Exception {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		ProblemDetail body = ProblemDetail.forStatusAndDetail(status, "Current request is not a multipart request");
		
		return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
	}

	/**
	 * Handles Jakarta ConstraintViolationExceptions thrown within the application code and not within the Spring Framework.
	 */
	@ExceptionHandler
	public ResponseEntity<Object> handleValidationException(ConstraintViolationException ex, WebRequest request)
			throws Exception {
		List<Violation> violations = cveTrait.createViolations(ex.getConstraintViolations());

		ProblemDetail body = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request content.");
		body.setType(ConstraintViolationProblem.TYPE);
		body.setTitle("Constraint Violation");
		body.setProperty("violations", violations);

		return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST, request);
	}

	/**
	 * Handles ThrowableProblem exceptions to map them to Spring Problem JSON support.
	 */
	@ExceptionHandler
	public ResponseEntity<Object> handleThrowableProblemException(ThrowableProblem ex, WebRequest request)
			throws Exception {
		HttpStatus status = HttpStatus.valueOf(ex.getStatus().getStatusCode());

		ProblemDetail body = ProblemDetail.forStatusAndDetail(status, ex.getDetail());
		body.setType(ex.getType());
		body.setTitle(ex.getTitle());
		body.setInstance(ex.getInstance());
		for (Map.Entry<String, Object> entry : ex.getParameters().entrySet()) {
			body.setProperty(entry.getKey(), entry.getValue());
		}

		return handleExceptionInternal(ex, body, HttpHeaders.EMPTY, status, request);
	}


}