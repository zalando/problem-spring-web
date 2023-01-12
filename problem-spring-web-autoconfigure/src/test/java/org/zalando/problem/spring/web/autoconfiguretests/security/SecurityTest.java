package org.zalando.problem.spring.web.autoconfiguretests.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.spring.common.MediaTypes;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
final class SecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldConfigureExceptionHandling() throws Exception {
        mockMvc.perform(get("/not-exists-url"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.title", is("Unauthorized")))
                .andExpect(jsonPath("$.detail", is("Full authentication is required to access this resource")));
    }

    @Test
    void shouldRespectResponseStatus() throws Exception {
        mockMvc.perform(get("/custom-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.detail", is("Custom NotFoundException")));
    }

    @Test
    void shouldReturnCorrectContentType() throws Exception {
        mockMvc.perform(get("/custom-not-found").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.PROBLEM_VALUE))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.title", is("Not Found")))
                .andExpect(jsonPath("$.detail", is("Custom NotFoundException")));
    }

    @SpringBootApplication
    static class TestApp {
        @RestController
        static class Controller {
            @GetMapping("/custom-not-found")
            void getException() {
                throw new NotFoundException();
            }
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        static class NotFoundException extends RuntimeException {
            public NotFoundException() {
                super("Custom NotFoundException");
            }
        }

        @Configuration(proxyBeanMethods = false)
        static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.authorizeRequests()
                        .mvcMatchers("/custom-not-found").permitAll()
                        .anyRequest().authenticated();
            }
        }
    }
}
