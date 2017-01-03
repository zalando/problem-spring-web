package org.zalando.problem.spring.web.advice.example;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gag.annotation.remark.Facepalm;
import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
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
        throw new RuntimeException("expected", new IllegalStateException());
    }

    @RequestMapping(path = "/nested-throwable", method = GET)
    public ResponseEntity<String> nestedThrowable() {
        try {
            try {
                throw newNullPointer();
            } catch (final NullPointerException e) {
                throw newIllegalArgument(e);
            }
        } catch (final IllegalArgumentException e) {
            throw newIllegalState(e);
        }
    }

    private IllegalStateException newIllegalState(final IllegalArgumentException e) {
        throw new IllegalStateException("Illegal State", e);
    }

    private IllegalArgumentException newIllegalArgument(final NullPointerException e) {
        throw new IllegalArgumentException("Illegal Argument", e);
    }

    private NullPointerException newNullPointer() {
        throw new NullPointerException("Null Pointer");
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

    }

    @RequestMapping(path = "/json-decimal")
    public void bigDecimal(@RequestBody final BigDecimal bigDecimal) {

    }

    @RequestMapping(path = "/json-user")
    public void user(@RequestBody final User user) {

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

    @RequestMapping("/handler-secured")
    public void secured() {

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

        @Size(min = 3, max = 10)
        public String getName() {
            return name;
        }

    }

    @Data
    static final class PageRequest {

        @Min(value = 0)
        private int page = 0;

        @Min(value = 1)
        private int size = 50;
    }

}
