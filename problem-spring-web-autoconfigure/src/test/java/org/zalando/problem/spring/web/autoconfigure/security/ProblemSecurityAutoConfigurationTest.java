package org.zalando.problem.spring.web.autoconfigure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;
import org.zalando.problem.spring.web.autoconfigure.ProblemAutoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ProblemSecurityAutoConfigurationTest {
    WebApplicationContextRunner contextRunner;

    @BeforeEach
    void setUp() {
        contextRunner = new WebApplicationContextRunner()
                .withConfiguration(
                        AutoConfigurations.of(ProblemSecurityAutoConfiguration.class,
                                ProblemAutoConfiguration.class,
                                SecurityAutoConfiguration.class));
    }

    @Test
    void shouldBeConfiguredDefaults() {
        contextRunner.withUserConfiguration(SecurityConfiguration.class)
                .run(context -> assertThat(context)
                        .hasSingleBean(SecurityProblemSupport.class)
                        .hasSingleBean(AdviceTrait.class)
                        .hasSingleBean(SecurityExceptionHandling.class)
                        .hasSingleBean(ProblemSecurityBeanPostProcessor.class));
    }

    @Test
    void shouldBeConfiguredWithWebSecurityConfigurerAdapter() {
        contextRunner.withUserConfiguration(WebSecurityConfigurerAdapterConfiguration.class)
                .run(context -> assertThat(context)
                        .hasSingleBean(SecurityProblemSupport.class)
                        .hasSingleBean(AdviceTrait.class)
                        .hasSingleBean(SecurityExceptionHandling.class)
                        .hasSingleBean(ProblemSecurityBeanPostProcessor.class));
    }

    @Configuration(proxyBeanMethods = false)
    static class SecurityConfiguration {
        @Bean
        HandlerExceptionResolver handlerExceptionResolver() {
            return mock(HandlerExceptionResolver.class);
        }
    }

    @Configuration(proxyBeanMethods = false)
    static class WebSecurityConfigurerAdapterConfiguration {
        @Bean
        HandlerExceptionResolver handlerExceptionResolver() {
            return mock(HandlerExceptionResolver.class);
        }

        @Configuration
        static class SecurityConfig {
            @Bean
            public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
                DefaultSecurityFilterChain build = http.build();
                return build;
            }
        }
    }
}