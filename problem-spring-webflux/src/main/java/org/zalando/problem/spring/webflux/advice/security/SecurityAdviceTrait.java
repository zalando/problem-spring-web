package org.zalando.problem.spring.webflux.advice.security;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
public interface SecurityAdviceTrait extends
        AuthenticationAdviceTrait,
        AccessDeniedAdviceTrait {

}
