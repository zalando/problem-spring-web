package org.zalando.problem.spring.web.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAutoConfiguration
@EnableWebMvc
final class CustomWebMvcApplicationTest {

    @Configuration
    static class TestApplication {

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
