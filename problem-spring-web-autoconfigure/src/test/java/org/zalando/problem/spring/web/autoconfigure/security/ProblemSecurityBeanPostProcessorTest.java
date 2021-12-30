package org.zalando.problem.spring.web.autoconfigure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.util.ReflectionTestUtils;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


class ProblemSecurityBeanPostProcessorTest {

    private ProblemSecurityBeanPostProcessor processor;
    private HttpSecurity http;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        processor = new ProblemSecurityBeanPostProcessor();
        final ObjectPostProcessor<Object> objectPostProcessor = mock(ObjectPostProcessor.class);
        final AuthenticationManagerBuilder authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);
        http = spy(new HttpSecurity(objectPostProcessor, authenticationManagerBuilder, new HashMap<>()));
    }

    @Test
    void shouldBeConfigured() throws Exception {
        assertThat(processor.postProcessAfterInitialization(http, "httpSecurity")).isEqualTo(http);
        verify(http).apply(any(ProblemHttpConfigurer.class));
    }

    @Test
    void shouldBeNotConfiguredWhenAlreadyConfigured() throws Exception {
        doReturn(new ProblemHttpConfigurer()).when(http).getConfigurer(ProblemHttpConfigurer.class);

        assertThat(processor.postProcessAfterInitialization(http, "httpSecurity")).isEqualTo(http);
        verify(http, never()).apply(any(ProblemHttpConfigurer.class));
    }

    @Test
    void shouldBeNotConfiguredWhenBeanIsNotHttpSecurity() {
        assertThat(processor.postProcessAfterInitialization("test", "notHttpSecurity")).isEqualTo("test");
    }
}
