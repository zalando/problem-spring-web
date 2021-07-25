package org.zalando.problem.spring.web.autoconfigure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.util.ReflectionTestUtils;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.HashMap;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


class ProblemSecurityBeanPostProcessorTest {

    private ObjectProvider<SecurityProblemSupport> provider;
    private ProblemSecurityBeanPostProcessor processor;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        provider = mock(ObjectProvider.class);
        processor = new ProblemSecurityBeanPostProcessor(provider);
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldBeConfigured() throws Exception {
        final ObjectPostProcessor<Object> objectPostProcessor = mock(ObjectPostProcessor.class);
        final AuthenticationManagerBuilder authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);
        final SecurityProblemSupport securityProblemSupport = mock(SecurityProblemSupport.class);
        final HttpSecurity http = new HttpSecurity(objectPostProcessor, authenticationManagerBuilder, new HashMap<>());

        doAnswer(answer -> {
            final Consumer<SecurityProblemSupport> consumer = (Consumer<SecurityProblemSupport>) answer.getArguments()[0];
            consumer.accept(securityProblemSupport);
            return null;
        }).when(provider).ifAvailable(any(Consumer.class));

        assertThat(processor.postProcessAfterInitialization(http, "httpSecurity")).isEqualTo(http);
        assertThat(ReflectionTestUtils.getField(http.exceptionHandling(), "authenticationEntryPoint")).isEqualTo(securityProblemSupport);
        assertThat(ReflectionTestUtils.getField(http.exceptionHandling(), "accessDeniedHandler")).isEqualTo(securityProblemSupport);
    }

    @Test
    void shouldBeNotConfiguredWhenBeanIsNotHttpSecurity() {
        assertThat(processor.postProcessAfterInitialization("test", "notHttpSecurity")).isEqualTo("test");
    }
}