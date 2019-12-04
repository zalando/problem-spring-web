package org.zalando.problem.spring.web.autoconfigure.security;

import org.apiguardian.api.API;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
@ControllerAdvice
final class SecurityExceptionHandling
        implements ProblemHandling, SecurityAdviceTrait {

}
