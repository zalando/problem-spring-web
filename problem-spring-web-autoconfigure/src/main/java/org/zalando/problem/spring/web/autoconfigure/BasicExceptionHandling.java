package org.zalando.problem.spring.web.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.spring.web.advice.ProblemHandling;

/**
 * Activates Problem library with default behaviour if there's no explicit configuration
 * in the user of the library
 */
@ControllerAdvice
@ConditionalOnMissingBean(AdviceTrait.class) //only if user doesn't declare their own
public class BasicExceptionHandling implements ProblemHandling {
}
