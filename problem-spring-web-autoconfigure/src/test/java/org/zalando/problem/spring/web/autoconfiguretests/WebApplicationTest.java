package org.zalando.problem.spring.web.autoconfiguretests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.spring.web.autoconfigure.ExceptionHandling;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
final class WebApplicationTest {

    @Configuration
    static class TestApplication {

    }

    @Test
    void shouldConfigureExceptionHandling(
            @Autowired final AdviceTrait trait) {
        assertThat(trait).isExactlyInstanceOf(ExceptionHandling.class);
    }

    @Test
    void shouldConfigureProblemModule(
            @Autowired final ProblemModule module) {
        assertThat(module).isNotNull();
    }

    @Test
    void shouldConfigureConstraintViolationProblemModule(
            @Autowired final ConstraintViolationProblemModule module) {
        assertThat(module).isNotNull();
    }

}
