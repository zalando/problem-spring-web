package org.zalando.problem.spring.web.autoconfigure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ClassUtils;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProblemHttpConfigurerTest {

    private ProblemHttpConfigurer configurer;
    private Map<Class<?>, Object> sharedObjects;
    private ApplicationContext applicationContext;
    private ObjectPostProcessor<Object> objectPostProcessor;
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        configurer = new ProblemHttpConfigurer();
        sharedObjects = new HashMap<>();
        applicationContext = mock(ApplicationContext.class);
        objectPostProcessor = mock(ObjectPostProcessor.class);
        authenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);
    }

    @Test
    void shouldBeRegisteredSpringFactoriesLoader() {
        assertThat(SpringFactoriesLoader.loadFactories(AbstractHttpConfigurer.class, ClassUtils.getDefaultClassLoader()))
                .hasAtLeastOneElementOfType(ProblemHttpConfigurer.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldBeConfigured() throws Exception {
        sharedObjects.put(ApplicationContext.class, applicationContext);
        final SecurityProblemSupport securityProblemSupport = mock(SecurityProblemSupport.class);
        final ObjectProvider<SecurityProblemSupport> beanProvider = mock(ObjectProvider.class);
        doReturn(beanProvider).when(applicationContext).getBeanProvider(SecurityProblemSupport.class);
        doAnswer(answer -> {
            final Consumer<SecurityProblemSupport> consumer = (Consumer<SecurityProblemSupport>) answer.getArguments()[0];
            consumer.accept(securityProblemSupport);
            return null;
        }).when(beanProvider).ifAvailable(any(Consumer.class));
        final HttpSecurity http = new HttpSecurity(objectPostProcessor, authenticationManagerBuilder, sharedObjects);

        configurer.init(http);

        assertThat(ReflectionTestUtils.getField(http.exceptionHandling(), "authenticationEntryPoint")).isEqualTo(securityProblemSupport);
        assertThat(ReflectionTestUtils.getField(http.exceptionHandling(), "accessDeniedHandler")).isEqualTo(securityProblemSupport);
    }


    @Test
    @SuppressWarnings("unchecked")
    void shouldBeNotConfiguredWhenSecurityProblemSupportNotExists() throws Exception {
        sharedObjects.put(ApplicationContext.class, applicationContext);
        final ObjectProvider<SecurityProblemSupport> beanProvider = mock(ObjectProvider.class);
        doReturn(beanProvider).when(applicationContext).getBeanProvider(SecurityProblemSupport.class);
        doReturn(null).when(beanProvider).getIfAvailable();
        final HttpSecurity http = new HttpSecurity(objectPostProcessor, authenticationManagerBuilder, sharedObjects);

        configurer.init(http);

        assertThat(ReflectionTestUtils.getField(http.exceptionHandling(), "authenticationEntryPoint")).isEqualTo(null);
        assertThat(ReflectionTestUtils.getField(http.exceptionHandling(), "accessDeniedHandler")).isEqualTo(null);
    }

    @Test
    void shouldBeNotConfiguredWhenApplicationContextNotExists() throws Exception {
        final HttpSecurity http = new HttpSecurity(objectPostProcessor, authenticationManagerBuilder, sharedObjects);

        configurer.init(http);

        assertThat(ReflectionTestUtils.getField(http.exceptionHandling(), "authenticationEntryPoint")).isEqualTo(null);
        assertThat(ReflectionTestUtils.getField(http.exceptionHandling(), "accessDeniedHandler")).isEqualTo(null);
    }
}