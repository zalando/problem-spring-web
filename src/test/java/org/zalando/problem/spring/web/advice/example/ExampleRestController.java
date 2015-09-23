package org.zalando.problem.spring.web.advice.example;

/*
 * #%L
 * problem-handling
 * %%
 * Copyright (C) 2015 Zalando SE
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gag.annotation.remark.Facepalm;
import com.google.gag.annotation.remark.Hack;
import com.google.gag.annotation.remark.OhNoYouDidnt;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.zalando.problem.spring.web.advice.custom.NotFoundException;

import javax.servlet.http.HttpServletRequest;
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
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.OffsetDateTime;
import java.util.Set;

@Validated
@RestController
@RequestMapping("/api")
public class ExampleRestController {

    @RequestMapping(value = "/handler-ok", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("ok");
    }

    @RequestMapping(value = "/handler-throwable", method = RequestMethod.GET)
    public ResponseEntity<String> throwable() {
        throw new RuntimeException("expected");
    }

    @RequestMapping(value = "/handler-problem", method = RequestMethod.GET)
    public ResponseEntity<String> problem() {
        throw new ExpectedProblem("Nothing out of the ordinary");
    }

    @RequestMapping(value = "/handler-put", method = RequestMethod.PUT)
    public ResponseEntity<String> put(@RequestBody final String body) {
        return ResponseEntity.ok(body);
    }

    @RequestMapping(value = "/noSuchRequestHandlingMethod", method = RequestMethod.GET)
    public ResponseEntity<Void> noSuchRequestHandlingMethod(final HttpServletRequest request)
            throws NoSuchRequestHandlingMethodException {
        // I have no clue how to trigger this naturally
        throw new NoSuchRequestHandlingMethodException(request);
    }

    @RequestMapping(value = "/handler-params", method = RequestMethod.GET)
    public ResponseEntity<Void> params(
            @RequestParam("params1") final String[] params1,
            @RequestParam("params2") final String[] params2) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/handler-headers", method = RequestMethod.GET)
    public ResponseEntity<Void> headers(
            @RequestHeader("X-Custom-Header") final String header1) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/handler-conversion", method = RequestMethod.GET)
    public ResponseEntity<Void> conversion(@RequestParam("dateTime") final OffsetDateTime dateTime) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/handler-multipart", method = RequestMethod.POST)
    public ResponseEntity<Void> conversion(
            @RequestPart("payload1") final MultipartFile payload1,
            @RequestPart("payload2") final MultipartFile payload2) {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/not-found", method = RequestMethod.GET)
    public ResponseEntity<String> notFound() {
        throw new NotFoundException("Unable to find entity");
    }

    @RequestMapping(value = "/handler-invalid-param", method = RequestMethod.POST)
    public void validRequestParam(@RequestBody final User user) {
        @Hack("I couldn't make Spring throw this implicitely using annotations...")
        @Facepalm
        @OhNoYouDidnt
        final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        final Set<ConstraintViolation<User>> violations = validator.validate(user);
        throw new ConstraintViolationException(violations);
    }

    @RequestMapping(value = "/handler-invalid-body", method = RequestMethod.POST)
    public ResponseEntity<String> validRequestBody(@Valid @RequestBody final User user) {
        return ResponseEntity.ok("done");
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

    public static final class NotBobConstraintValidator implements ConstraintValidator<NotBob, User> {

        @Override
        public void initialize(NotBob constraintAnnotation) {

        }

        @Override
        public boolean isValid(User user, ConstraintValidatorContext context) {
            return !"Bob".equals(user.getName());
        }

    }

    @NotBob
    public static final class User {

        private final String name;

        @JsonCreator
        public User(@JsonProperty("name") String name) {
            this.name = name;
        }

        @Size(min = 3, max = 10)
        public String getName() {
            return name;
        }

    }

}
