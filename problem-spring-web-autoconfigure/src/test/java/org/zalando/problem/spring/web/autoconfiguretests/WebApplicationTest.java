package org.zalando.problem.spring.web.autoconfiguretests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.spring.web.autoconfigure.ExceptionHandling;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = SecurityAutoConfiguration.class)
final class WebApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Configuration
    static class TestApplication {
        @RestController
        static class Controller {
            @GetMapping("/exception")
            void getException() {
                throw new RuntimeException("An exception from GET");
            }
        }
    }

    @Test
    void shouldConfigureExceptionHandling(
            @Autowired final AdviceTrait trait) throws Exception {
        assertThat(trait).isExactlyInstanceOf(ExceptionHandling.class);
        mockMvc.perform(get("/exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.title", is("Internal Server Error")))
                .andExpect(jsonPath("$.detail", is("An exception from GET")));
    }

    @Test
    void shouldConfigureProblemModule(
            @Autowired final ProblemModule module) {
        assertThat(module).isNotNull();
    }

    @Test
    void shouldConfigureConstraintViolationProblemModule(
            @Autowired final ConstraintViolationProblemModule module) {
        assertThat(module).isNotNull();
    }

}
