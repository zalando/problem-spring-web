package org.zalando.problem.spring.web.autoconfigure;

import org.apiguardian.api.API;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
@ControllerAdvice
public final class ExceptionHandling implements ProblemHandling {

}
