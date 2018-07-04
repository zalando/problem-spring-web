package org.zalando.problem.spring.webflux.advice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apiguardian.api.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.spring.webflux.advice.utils.AdviceUtils;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
@Component
public class SecurityProblemSupport implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {

    private final SecurityAdviceTrait advice;

    private final ObjectMapper mapper;

    @Autowired
    public SecurityProblemSupport(SecurityAdviceTrait advice, ObjectMapper mapper) {
        this.advice = advice;
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return advice.handleAuthentication(e, exchange)
                .flatMap(entity -> AdviceUtils.setHttpResponse(entity, exchange, mapper));
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException e) {
        return advice.handleAccessDenied(e, exchange)
                .flatMap(entity -> AdviceUtils.setHttpResponse(entity, exchange, mapper));
    }

}
