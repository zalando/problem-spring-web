package org.zalando.problem.spring.web.autoconfigure;

import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(WebSecurityConfigurer.class)
public class ProblemAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AdviceTrait.class)
    public AdviceTrait exceptionHandling() {
        return new ExceptionHandling();
    }

}
