package org.zalando.problem.spring.web.advice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
import org.zalando.problem.jackson.ProblemModule;
import org.zalando.problem.spring.common.MediaTypes;
import org.zalando.problem.spring.web.advice.ProblemHandling;

import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebAppConfiguration
final class SecurityAdviceTraitTest {

    @BeforeEach
    void setUp() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({MvcConfiguration.class, SecurityConfiguration.class})
    public static class TestConfiguration extends WebMvcConfigurationSupport {
        @Bean
        public MockMvc mvc(final WebApplicationContext context) {
            return MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(springSecurity())
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
    @Import(SecurityProblemSupport.class)
    public static class SecurityConfiguration {

        @Autowired
        private SecurityProblemSupport problemSupport;

        @Bean
        public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
            http.csrf().disable();
            http.httpBasic().disable();
            http.sessionManagement().disable();
            http.authorizeRequests()
                    .requestMatchers("/greet").hasRole("ADMIN")
                    .anyRequest().authenticated();
            http.exceptionHandling()
                    .authenticationEntryPoint(problemSupport)
                    .accessDeniedHandler(problemSupport);
            http.addFilterBefore(new AuthenticationFilter(problemSupport), LogoutFilter.class);
            return http.build();
        }

    }
    
    public static class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

		protected AuthenticationFilter(SecurityProblemSupport problemSupport) {
			super(new AntPathRequestMatcher("/authFilter"));
			setAuthenticationFailureHandler(problemSupport);
		}

		@Override
		public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
				throws AuthenticationException {
			throw new BadCredentialsException("invalid pass");
		}
    	
    }

    @ControllerAdvice
    public static class ExceptionHandling implements ProblemHandling, SecurityAdviceTrait {

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
    void notAuthenticated() throws Exception {
        mvc.perform(post("/").with(anonymous()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(jsonPath("$.title", is("Unauthorized")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.detail", is("Full authentication is required to access this resource")));
    }

    @Test
    void notAuthorizedByRole() throws Exception {
        mvc.perform(get("/greet").param("name", "Alice").with(user("user").roles("USER")))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(jsonPath("$.title", is("Forbidden")))
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.detail", is("Access is denied")));
    }

    @Test
    void notAuthorizedByFilter() throws Exception {
        mvc.perform(get("/authFilter").param("name", "Alice").with(user("user").roles("ADMIN")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaTypes.PROBLEM))
                .andExpect(jsonPath("$.title", is("Unauthorized")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.detail", is("invalid pass")));
    }
}
