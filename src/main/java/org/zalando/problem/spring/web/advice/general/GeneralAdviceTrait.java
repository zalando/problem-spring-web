package org.zalando.problem.spring.web.advice.general;

import org.zalando.problem.spring.web.advice.AdviceTrait;

/**
 * @see AdviceTrait
 */
public interface GeneralAdviceTrait extends
        ProblemAdviceTrait,
        ThrowableAdviceTrait,
        UnsupportedOperationAdviceTrait {
}
