package org.zalando.problem.spring.web.advice.general;

import org.apiguardian.api.API;
import org.zalando.problem.spring.web.advice.AdviceTrait;

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
