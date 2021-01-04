package org.zalando.problem.spring.webflux.advice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurationSupport;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.webflux.advice.ProblemHandling;

@SpringJUnitConfig
@WebAppConfiguration
final class SecurityAdviceTraitTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void notAuthenticated() {
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Unauthorized")
                .jsonPath("$.status").isEqualTo(HttpStatus.UNAUTHORIZED.value())
                .jsonPath("$.detail").isEqualTo("Not Authenticated");
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    void authorized() {
        webTestClient.get().uri("/greet?name=alice")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser(username = "user")
    void notAuthorized() {
        webTestClient.get().uri("/greet?name=alice")
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().contentType(MediaTypes.PROBLEM)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Forbidden")
                .jsonPath("$.status").isEqualTo(HttpStatus.FORBIDDEN.value())
                .jsonPath("$.detail").isEqualTo("Access Denied");
    }

    @Configuration
    @EnableWebFlux
    @EnableWebFluxSecurity
    @Import({WebFluxConfiguration.class, SecurityConfiguration.class})
    public static class TestConfiguration extends WebFluxConfigurationSupport {

        @Bean
        public WebTestClient webTestClient(final WebApplicationContext context) {
            return WebTestClient.bindToApplicationContext(context).build();
        }
    }

    @Configuration
    @Import({TestController.class, ExceptionHandling.class})
    public static class WebFluxConfiguration extends WebFluxConfigurationSupport {

        @Bean
        public ObjectMapper mapper() {
            return new ObjectMapper().registerModule(new ProblemModule());
        }

    }

    @Configuration
    @Import(SecurityProblemSupport.class)
    public static class SecurityConfiguration {

        @Autowired
        private SecurityProblemSupport problemSupport;

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http.csrf().disable()
                    .authorizeExchange()
                    .pathMatchers("/greet").hasRole("ADMIN")
                    .anyExchange().authenticated()
                    .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(problemSupport)
                    .accessDeniedHandler(problemSupport)
                    .and().build();
        }

    }

    @ControllerAdvice
    static class ExceptionHandling implements ProblemHandling, SecurityAdviceTrait {

    }

    @RestController
    public static class TestController {

        @RequestMapping("/greet")
        public String greet(@RequestParam final String name) {
            return "Hello " + name + "!";
        }

    }

}
