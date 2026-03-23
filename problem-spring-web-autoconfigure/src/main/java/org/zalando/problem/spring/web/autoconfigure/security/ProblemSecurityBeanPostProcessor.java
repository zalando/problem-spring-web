package org.zalando.problem.spring.web.autoconfigure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.util.ClassUtils;

import java.util.Objects;

@Slf4j
public class ProblemSecurityBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (!ClassUtils.isAssignableValue(HttpSecurity.class, bean)) {
            return bean;
        }
        final HttpSecurity http = (HttpSecurity) bean;
        try {
            final ProblemHttpConfigurer configurer = http.getConfigurer(ProblemHttpConfigurer.class);
            if (Objects.isNull(configurer)) {
                http.apply(new ProblemHttpConfigurer());
            }
        } catch (final Exception cause) {
            throw new BeanCreationException("Fail to configure HttpSecurity's exceptionHandling", cause);
        }
        return bean;
    }

}
