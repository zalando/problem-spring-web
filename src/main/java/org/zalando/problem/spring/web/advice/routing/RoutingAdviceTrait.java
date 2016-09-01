package org.zalando.problem.spring.web.advice.routing;

import org.zalando.problem.spring.web.advice.AdviceTrait;

/**
 * @see AdviceTrait
 */
public interface RoutingAdviceTrait extends
        MissingServletRequestParameterAdviceTrait,
        MissingServletRequestPartAdviceTrait,
        NoHandlerFoundAdviceTrait,
        NoSuchRequestHandlingMethodAdviceTrait,
        ServletRequestBindingAdviceTrait {
}
