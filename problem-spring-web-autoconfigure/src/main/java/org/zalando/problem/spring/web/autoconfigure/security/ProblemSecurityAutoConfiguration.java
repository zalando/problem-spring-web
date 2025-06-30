package org.zalando.problem.spring.web.autoconfigure.security;

import org.apiguardian.api.API;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
import org.zalando.problem.spring.web.autoconfigure.ProblemAutoConfiguration;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass(WebSecurityConfigurer.class)
@ConditionalOnBean(WebSecurityConfiguration.class)
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@AutoConfigureBefore(ProblemAutoConfiguration.class)
public class ProblemSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AdviceTrait.class)
    public AdviceTrait securityExceptionHandling() {
        return new SecurityExceptionHandling();
    }

    @Bean
    public SecurityProblemSupport securityProblemSupport(@Qualifier("handlerExceptionResolver") final HandlerExceptionResolver resolver,
                                                         final AdviceTrait adviceTrait) {
        return new SecurityProblemSupport(resolver);
    }

    @Bean
    public ProblemSecurityBeanPostProcessor problemSecurityBeanPostProcessor() {
        return new ProblemSecurityBeanPostProcessor();
    }
}
