package org.zalando.problem.spring.webflux.advice.network;

import org.apiguardian.api.API;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see AdviceTrait
 */
@API(status = STABLE)
public interface NetworkAdviceTrait extends
        SocketTimeoutAdviceTrait {

}
