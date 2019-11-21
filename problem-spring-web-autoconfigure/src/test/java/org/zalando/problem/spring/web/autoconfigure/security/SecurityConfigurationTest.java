package org.zalando.problem.spring.web.autoconfigure.security;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest("spring.main.web-application-type=none")
final class SecurityConfigurationTest {

    @Test
    void contextLoads(@Autowired ApplicationContext context) {
        assertThat(context).isNotNull();
    }

    @SpringBootApplication
    static class TestApplication {
    }
}