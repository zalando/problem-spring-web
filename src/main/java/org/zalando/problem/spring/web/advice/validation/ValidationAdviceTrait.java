package org.zalando.problem.spring.web.advice.validation;

import org.zalando.problem.spring.web.advice.AdviceTrait;

/**
 * @see AdviceTrait
 */
public interface ValidationAdviceTrait extends
        ConstraintViolationAdviceTrait,
        MethodArgumentNotValidAdviceTrait {
}
