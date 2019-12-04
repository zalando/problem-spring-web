package org.zalando.problem.spring.web.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(webEnvironment = NONE)
@EnableAutoConfiguration
final class NonWebApplicationTest {

    @Configuration
    static class TestApplication {

    }

    @Test
    void shouldNotConfigureDefaultExceptionHandling(
            @Autowired @Nullable final AdviceTrait trait) {
        assertThat(trait).isNull();
    }

    @Test
    void shouldNotConfigureProblemModule(
            @Autowired @Nullable final ProblemModule module) {
        assertThat(module).isNull();
    }

    @Test
    void shouldNotConfigureConstraintViolationProblemModule(
            @Autowired @Nullable final ConstraintViolationProblemModule module) {
        assertThat(module).isNull();
    }

}
