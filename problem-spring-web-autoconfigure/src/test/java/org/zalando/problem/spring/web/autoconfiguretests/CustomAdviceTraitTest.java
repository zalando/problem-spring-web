package org.zalando.problem.spring.web.autoconfiguretests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
final class CustomAdviceTraitTest {

    @Configuration
    static class TestApplication {

        @Bean
        public CustomExceptionHandling customExceptionHandling() {
            return new CustomExceptionHandling();
        }

    }

    @ControllerAdvice
    private static final class CustomExceptionHandling implements ProblemHandling {

    }

    @Test
    void shouldNotConfigureDefaultExceptionHandling(
            @Autowired final AdviceTrait trait) {
        assertThat(trait).isInstanceOf(CustomExceptionHandling.class);
    }

}
