package org.zalando.problem.spring.web.autoconfigure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.ClassUtils;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Slf4j
@RequiredArgsConstructor
public class ProblemSecurityBeanPostProcessor implements BeanPostProcessor {
    private final ObjectProvider<SecurityProblemSupport> securityProblemSupport;

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (ClassUtils.isAssignableValue(HttpSecurity.class, bean)) {
            securityProblemSupport.ifAvailable(support -> register((HttpSecurity) bean, beanName, support));
        }
        return bean;
    }

    private void register(final HttpSecurity http, final String beanName, final SecurityProblemSupport support) {
        try {
            http.exceptionHandling().authenticationEntryPoint(support).accessDeniedHandler(support);
        } catch (final Exception cause) {
            throw new BeanCreationException(beanName, cause);
        }
        log.info("Register HttpSecurity's exceptionHandling");
    }
}
