package org.zalando.problem.spring.web.advice.example;

import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.report.ValidationReport.Message;
import com.atlassian.oai.validator.springmvc.InvalidRequestException;
import com.atlassian.oai.validator.springmvc.InvalidResponseException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gag.annotation.remark.Facepalm;
import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import dev.failsafe.CircuitBreaker;
import dev.failsafe.Failsafe;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Validated
@RestController
@RequestMapping("/api")
public class ExampleRestController {

    @RequestMapping(path = "/handler-ok",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(path = "/handler-throwable", method = GET)
    public ResponseEntity<String> throwable() {
        throw new IllegalArgumentException("expected", new IllegalStateException());
    }

    @RequestMapping(path = "/handler-throwable-annotated", method = GET)
    public ResponseEntity<String> throwableAnnotated() {
        throw new MyException(new IllegalArgumentException("expected", new IllegalStateException()));
    }

    @RequestMapping(path = "/handler-throwable-annotated-cause", method = GET)
    public ResponseEntity<String> throwableAnnotatedCause() {
        throw new IllegalArgumentException("expected", new IllegalStateException(new MyException()));
    }

    @RequestMapping(path = "/handler-throwable-annotated-reason", method = GET)
    public ResponseEntity<String> throwableAnnotatedWithReason() {
        throw new MyExceptionWithReason(new IllegalArgumentException("expected", new IllegalStateException()));
    }

    @RequestMapping(path = "/handler-throwable-extended", method = GET)
    public ResponseEntity<String> throwableExtended() {
        throw new MyResponseStatusException();
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    private static final class MyException extends RuntimeException {
        MyException() {
        }

        MyException(final Throwable cause) {
            super(cause);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED, reason = "Test reason")
    private static final class MyExceptionWithReason extends RuntimeException {
        MyExceptionWithReason(final Throwable cause) {
            super(cause);
        }
    }

    private static final class MyResponseStatusException extends ResponseStatusException {
        MyResponseStatusException() {
            super(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    @RequestMapping(path = "/nested-throwable", method = GET)
    public ResponseEntity<String> nestedThrowable() {
        try {
            try {
                throw newNoSuchElement();
            } catch (final NoSuchElementException e) {
                throw newIllegalArgument(e);
            }
        } catch (final IllegalArgumentException e) {
            throw newIllegalState(e);
        }
    }

    @RequestMapping(method = GET, path = "/socket-timeout")
    public ResponseEntity<String> socketTimeout() throws SocketTimeoutException {
        throw new SocketTimeoutException();
    }

    private IllegalStateException newIllegalState(final Exception e) {
        throw new IllegalStateException("Illegal State", e);
    }

    private IllegalArgumentException newIllegalArgument(final Exception e) {
        throw new IllegalArgumentException("Illegal Argument", e);
    }

    private NoSuchElementException newNoSuchElement() {
        throw new NoSuchElementException("No such element");
    }

    @RequestMapping(path = "/handler-problem", method = GET)
    public ResponseEntity<String> problem() {
        throw new ExpectedProblem("Nothing out of the ordinary");
    }

    @RequestMapping(path = "/handler-put", method = PUT, consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE})
    public ResponseEntity<String> put(@RequestBody final String body) {
        return ResponseEntity.ok(body);
    }

    @RequestMapping(path = "/json-object")
    public void jsonObject(@RequestBody final Map<String, Object> body) {
        // no response needed for testing
    }

    @RequestMapping(path = "/json-decimal")
    public void bigDecimal(@RequestBody final BigDecimal bigDecimal) {
        // no response needed for testing
    }

    @RequestMapping(path = "/json-user")
    public void user(@RequestBody final User user) {
        // no response needed for testing
    }

    @RequestMapping(path = "/handler-params", method = GET)
    public ResponseEntity<Void> params(
            @RequestParam("params1") final String[] params1,
            @RequestParam("params2") final String[] params2) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/handler-headers", method = GET)
    public ResponseEntity<Void> headers(
            @RequestHeader("X-Custom-Header") final String header1) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/handler-conversion", method = GET)
    public ResponseEntity<Void> conversion(@RequestParam("dateTime") final OffsetDateTime dateTime) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/handler-multipart", method = POST)
    public ResponseEntity<Void> conversion(
            @RequestPart("payload1") final MultipartFile payload1,
            @RequestPart("payload2") final MultipartFile payload2) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/handler-invalid-param", method = POST)
    public void validRequestParam(@RequestBody final UserRequest user) {
        @Hack("I couldn't make Spring throw this implicitly using annotations...")
        @Facepalm
        @OhNoYouDidnt
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final Set<ConstraintViolation<UserRequest>> violations = validator.validate(user);
        throw new ConstraintViolationException(violations);
    }

    @RequestMapping("/handler-circuit-breaker-open")
    public void circuitBreakerOpen() {
        final CircuitBreaker<Object> breaker = CircuitBreaker.builder().build();
        breaker.open();

        Failsafe.with(breaker)
                .run(() -> {});
    }

    @RequestMapping(method = POST, path = "/openapi/invalid-request")
    public void invalidRequest() {
        throw new InvalidRequestException(ValidationReport.from(
                Message.create("foo", "not null").build()));
    }

    @RequestMapping(method = POST, path = "/openapi/invalid-response")
    public void invalidResponse() {
        throw new InvalidResponseException(ValidationReport.from(
                Message.create("foo", "not null").build()));
    }

    @RequestMapping("/handler-secured")
    public void secured() {
        // no response needed for testing
    }

    @RequestMapping(path = "/handler-invalid-body", method = POST)
    public ResponseEntity<String> validRequestBody(@Valid @RequestBody final UserRequest user) {
        // TODO find a way to change the "object name" of the body, by default it's the lower-camel-cased class name
        return ResponseEntity.ok("done");
    }

    @RequestMapping(path = "/handler-invalid-query-strings", method = GET)
    public ResponseEntity<String> validRequestQueryStrings(@Valid final PageRequest pageRequest) {
        return ResponseEntity.ok("done");
    }

    @RequestMapping("/not-implemented")
    public void notImplemented() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Documented
    @Constraint(validatedBy = NotBobConstraintValidator.class)
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @ReportAsSingleViolation
    public @interface NotBob {

        String message() default "must not be called Bob";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};

    }

    public static final class NotBobConstraintValidator implements ConstraintValidator<NotBob, UserRequest> {

        @Override
        public void initialize(final NotBob constraintAnnotation) {
            // no initialization needed
        }

        @Override
        public boolean isValid(final UserRequest user, final ConstraintValidatorContext context) {
            return !"Bob".equals(user.getName());
        }

    }

    @NotBob
    public static final class UserRequest {

        private final String name;

        @JsonCreator
        public UserRequest(@JsonProperty("name") final String name) {
            this.name = name;
        }

        @Size(min = 3, max = 10) String getName() {
            return name;
        }

    }

    @Data
    private static final class PageRequest {

        @Min(value = 0)
        private int page = 0;

        @Min(value = 1)
        private int size = 50;
    }

}
