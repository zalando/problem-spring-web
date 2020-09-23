package org.zalando.problem.spring.web.autoconfigure;

import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(status = INTERNAL)
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
public class ProblemAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AdviceTrait.class)
    public AdviceTrait exceptionHandling() {
        return new ExceptionHandling();
    }

}
