package org.zalando.problem.spring.web.autoconfigure;

import org.apiguardian.api.API;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.springframework.boot.autoconfigure.web.ErrorProperties.IncludeAttribute.ALWAYS;

/**
 * Registers Problem Jackson modules when {@link WebMvcAutoConfiguration} is
 * enabled.
 */
@API(status = INTERNAL)
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnMissingBean(ProblemJacksonWebMvcAutoConfiguration.class)
public class ProblemJacksonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ProblemModule.class)
    public ProblemModule problemModule(ObjectProvider<ServerProperties> serverProperties) {
        ServerProperties props = serverProperties.getIfAvailable();
        return new ProblemModule().withStackTraces(props != null && props.getError().getIncludeStacktrace().equals(ALWAYS));
    }

    @Bean
    @ConditionalOnMissingBean(ConstraintViolationProblemModule.class)
    public ConstraintViolationProblemModule constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }

}
