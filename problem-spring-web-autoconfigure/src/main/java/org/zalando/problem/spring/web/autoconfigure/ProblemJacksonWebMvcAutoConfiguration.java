package org.zalando.problem.spring.web.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

import java.util.List;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * Registers Problem Jackson modules when using {@link EnableWebMvc}, which
 * deactivates {@link WebMvcAutoConfiguration}.
 */
@API(status = INTERNAL)
@Configuration
@ConditionalOnWebApplication
@ConditionalOnBean(WebMvcConfigurationSupport.class)
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class ProblemJacksonWebMvcAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
                .modules(
                        new ProblemModule(),
                        new ConstraintViolationProblemModule())
                .build();

        converters.add(new MappingJackson2HttpMessageConverter(mapper));
    }

}
