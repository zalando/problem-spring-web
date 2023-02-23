package org.zalando.problem.spring.web.autoconfigure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
final class SecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldConfigureExceptionHandling() throws Exception{
        mockMvc.perform(get("/not-exists-url"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.title", is("Unauthorized")))
                .andExpect(jsonPath("$.detail", is("Full authentication is required to access this resource")));
    }

    @SpringBootApplication
    static class TestApp {
        @Configuration(proxyBeanMethods = false)
        class WebSecurityConfig {
            @Bean
            protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
                http.authorizeRequests().anyRequest().authenticated();
                return http.build();
            }
        }
    }
}
