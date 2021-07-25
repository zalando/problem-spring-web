package org.zalando.problem.spring.web.autoconfigure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Slf4j
public class ProblemHttpConfigurer extends AbstractHttpConfigurer<ProblemHttpConfigurer, HttpSecurity> {

    @Override
    public void init(final HttpSecurity http) {
        final ApplicationContext applicationContext = http.getSharedObject(ApplicationContext.class);
        if (applicationContext == null) {
            log.warn("HttpSecurity's SharedObject doesn't have ApplicationContext");
            return;
        }
        final ObjectProvider<SecurityProblemSupport> provider = applicationContext.getBeanProvider(SecurityProblemSupport.class);
        provider.ifAvailable(support -> {
            try {
                http.exceptionHandling().authenticationEntryPoint(support).accessDeniedHandler(support);
            } catch (final Exception cause) {
                throw new BeanInstantiationException(getClass(), "Fail to register HttpSecurity's exceptionHandling", cause);
            }
            log.info("ProblemHttpConfigurer register HttpSecurity's exceptionHandling");
        });
    }
}
