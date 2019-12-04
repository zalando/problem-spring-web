package org.zalando.problem.spring.web.autoconfigure;

import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Registers Problem Jackson modules when {@link WebMvcAutoConfiguration} is
 * enabled.
 */
@API(status = INTERNAL)
@Configuration
@ConditionalOnWebApplication
@ConditionalOnMissingBean(ProblemJacksonWebMvcAutoConfiguration.class)
public class ProblemJacksonAutoConfiguration {

    @Bean
    public ProblemModule problemModule() {
        return new ProblemModule();
    }

    @Bean
    public ConstraintViolationProblemModule constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }

}
