package org.zalando.problem.spring.web.advice.http;

import org.zalando.problem.spring.web.advice.AdviceTrait;

/**
 * @see AdviceTrait
 */
public interface HttpAdviceTrait extends
        NotAcceptableAdviceTrait,
        UnsupportedMediaTypeAdviceTrait,
        MethodNotAllowedAdviceTrait {

}
