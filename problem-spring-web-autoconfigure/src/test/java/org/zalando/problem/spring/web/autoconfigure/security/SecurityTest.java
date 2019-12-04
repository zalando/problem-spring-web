package org.zalando.problem.spring.web.autoconfigure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableAutoConfiguration
final class SecurityTest {

    @Configuration
    static class TestApplication extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(final HttpSecurity http) throws Exception {
            http.csrf().disable();
            http.httpBasic().disable();
            http.sessionManagement().disable();
            http.authorizeRequests()
                    .anyRequest().authenticated();
        }

    }

    @Test
    void shouldConfigureExceptionHandling(
            @Autowired final AdviceTrait trait) {
        assertThat(trait).isExactlyInstanceOf(SecurityExceptionHandling.class);
    }

}
