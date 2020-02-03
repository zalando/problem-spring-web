package org.zalando.problem.spring.web.autoconfigure.security;

import lombok.AllArgsConstructor;
import org.apiguardian.api.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
import org.zalando.problem.spring.web.autoconfigure.ProblemAutoConfiguration;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnClass(WebSecurityConfigurer.class)
@ConditionalOnBean(WebSecurityConfiguration.class)
@Import(SecurityProblemSupport.class)
//subtract random, uncommon number to reduce chances of collision with a user-selected order
@Order(Ordered.LOWEST_PRECEDENCE - 21)
@AutoConfigureAfter(SecurityAutoConfiguration.class)
@AutoConfigureBefore(ProblemAutoConfiguration.class)
public class ProblemSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport support;

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(support)
                .accessDeniedHandler(support);
    }

    @Bean
    @ConditionalOnMissingBean(AdviceTrait.class)
    public AdviceTrait securityExceptionHandling() {
        return new SecurityExceptionHandling();
    }

}
