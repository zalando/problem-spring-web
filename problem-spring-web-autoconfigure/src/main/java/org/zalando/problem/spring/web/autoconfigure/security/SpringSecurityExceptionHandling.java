package org.zalando.problem.spring.web.autoconfigure.security;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.AdviceTrait;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;
import org.zalando.problem.spring.web.autoconfigure.BasicExceptionHandling;

/**
 * Activates Problem library with default behaviour in spring-security if there's no explicit configuration
 * in the user of the library
 */
@ControllerAdvice
@ConditionalOnClass(WebSecurityConfigurerAdapter.class) //only when spring-security is in classpath
@ConditionalOnMissingBean({SecurityAdviceTrait.class, AdviceTrait.class}) //only if user doesn't declare their own
@AutoConfigureBefore(BasicExceptionHandling.class)
public class SpringSecurityExceptionHandling extends BasicExceptionHandling implements SecurityAdviceTrait {
}
