package org.zalando.problem.spring.web.advice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.web.advice.MediaTypes;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import javax.servlet.Filter;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public final class SecurityAdviceTraitTest {

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({MvcConfiguration.class, SecurityConfiguration.class})
    public static class TestConfiguration extends WebMvcConfigurationSupport {

        @Autowired
        @Qualifier("springSecurityFilterChain")
        private Filter securityFilter;

        @Bean
        public MockMvc mvc(final WebApplicationContext context) {
            return MockMvcBuilders
                    .webAppContextSetup(context)
                    .addFilter(securityFilter)
                    .build();
        }
    }

    @Configuration
    @Import({TestController.class, ExceptionHandling.class})
    public static class MvcConfiguration extends WebMvcConfigurationSupport {

        @Override
        protected void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
            converters.clear();
            converters.add(new MappingJackson2HttpMessageConverter(new ObjectMapper()
                .registerModule(new ProblemModule())));
        }

    }

    @Configuration
    @EnableResourceServer
    @Import(ProblemAuthenticationEntryPoint.class)
    public static class SecurityConfiguration extends WebSecurityConfigurerAdapter implements ResourceServerConfigurer {

        @Autowired
        private ProblemAuthenticationEntryPoint entryPoint;

        @Override
        public void configure(final ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId("test");
        }

        @Override
        public void configure(final HttpSecurity http) throws Exception {
            http.authorizeRequests().anyRequest().denyAll();
            http.exceptionHandling().authenticationEntryPoint(entryPoint);
        }

    }

    @ControllerAdvice
    public static class ExceptionHandling implements ProblemHandling {

    }

    @RestController
    public static class TestController {

        @RequestMapping("/greet")
        public String greet(@RequestParam final String name) {
            return "Hello " + name + "!";
        }

    }

    @Autowired
    private MockMvc mvc;

    @Test
    public void notAuthenticated() throws Exception {
        mvc.perform(get("/greet")
                .param("name", "Alice"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(jsonPath("$.title", is("Forbidden")))
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.detail", is("Full authentication is required to access this resource")));
    }

}