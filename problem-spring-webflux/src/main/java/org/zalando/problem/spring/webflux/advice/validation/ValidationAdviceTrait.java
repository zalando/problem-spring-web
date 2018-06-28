package org.zalando.problem.spring.webflux.advice.validation;

import org.apiguardian.api.API;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * Advice trait to handle any validation exceptions.
 *
 * @see AdviceTrait
 */
@API(status = STABLE)
public interface ValidationAdviceTrait extends
        ConstraintViolationAdviceTrait,
        BindAdviceTrait {
}
