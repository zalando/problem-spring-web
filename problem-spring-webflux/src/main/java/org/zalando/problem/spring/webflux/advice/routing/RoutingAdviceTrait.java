package org.zalando.problem.spring.webflux.advice.routing;

import org.apiguardian.api.API;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see AdviceTrait
 */
@API(status = STABLE)
public interface RoutingAdviceTrait extends
        MissingServletRequestParameterAdviceTrait,
        MissingServletRequestPartAdviceTrait,
        ServletRequestBindingAdviceTrait {
}
