package org.zalando.problem.spring.web.advice.validation;

import org.apiguardian.api.API;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import static org.apiguardian.api.API.Status.STABLE;

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
@API(status = STABLE)
public interface ValidationAdviceTrait extends
        ConstraintViolationAdviceTrait,
        BindAdviceTrait,
        MethodArgumentNotValidAdviceTrait {
}
