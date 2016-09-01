package org.zalando.problem.spring.web.advice.io;

import org.zalando.problem.spring.web.advice.AdviceTrait;

/**
 * @see AdviceTrait
 */
public interface IOAdviceTrait extends
        MessageNotReadableAdviceTrait,
        MultipartAdviceTrait,
        TypeMistmatchAdviceTrait {
}
