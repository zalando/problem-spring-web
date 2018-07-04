package org.zalando.problem.spring.webflux.advice.general;

import org.apiguardian.api.API;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see AdviceTrait
 */
@API(status = STABLE)
public interface GeneralAdviceTrait extends
        ProblemAdviceTrait,
        ThrowableAdviceTrait,
        UnsupportedOperationAdviceTrait {
}
