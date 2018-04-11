package org.zalando.problem.spring.web.advice.validation;

import org.zalando.problem.spring.web.advice.AdviceTrait;

/**
 * Advice trait to handle any validation exceptions.
 * <p>
 * Be careful if you use {@link org.springframework.validation.beanvalidation.MethodValidationPostProcessor}
 * in order to validate method parameter field directly but {@code violations[].field} value looks like {@code arg0}
 * instead of parameter name, you have to configure a
 * {@link org.springframework.validation.beanvalidation.LocalValidatorFactoryBean} with your
 * {@link org.springframework.validation.beanvalidation.MethodValidationPostProcessor} like following:
 *
 * <pre><code>
 * {@literal @}Bean
 *  public Validator validator() {
 *      return new LocalValidatorFactoryBean();
 *  }
 *
 * {@literal @}Bean
 *  public MethodValidationPostProcessor methodValidationPostProcessor() {
 *      MethodValidationPostProcessor methodValidationPostProcessor = new MethodValidationPostProcessor();
 *      methodValidationPostProcessor.setValidator(validator());
 *      return methodValidationPostProcessor;
 *  }
 * </code></pre>
 *
 * @see AdviceTrait
 */
public interface ValidationAdviceTrait extends
        ConstraintViolationAdviceTrait,
        BindAdviceTrait,
        MethodArgumentNotValidAdviceTrait {
}
