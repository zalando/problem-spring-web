package org.zalando.problem.spring.web.autoconfigure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

/**
 * Registers exception handling in spring-security
 */
@Configuration
@ConditionalOnClass(WebSecurityConfigurerAdapter.class) //only when spring-security is in classpath
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;

    @Autowired
    public SecurityConfiguration(SecurityProblemSupport problemSupport) {
        super(true);
        this.problemSupport = problemSupport;
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport);
    }
}
