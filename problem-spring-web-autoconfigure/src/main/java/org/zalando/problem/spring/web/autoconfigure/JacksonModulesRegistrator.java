package org.zalando.problem.spring.web.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

/**
 * Registers Jackson modules when WebMvc AutoConfig is enabled
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnMissingBean(JacksonModulesRegistratorWithoutMvcAutoConfig.class)
public class JacksonModulesRegistrator {
    @Bean
    public ProblemModule problemModule() {
        return new ProblemModule();
    }

    @Bean
    public ConstraintViolationProblemModule constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }
}
