package org.zalando.problem.spring.web.advice;

import org.zalando.problem.spring.web.advice.custom.CustomAdviceTrait;
import org.zalando.problem.spring.web.advice.general.GeneralAdviceTrait;
import org.zalando.problem.spring.web.advice.http.HttpAdviceTrait;
import org.zalando.problem.spring.web.advice.io.IOAdviceTrait;
import org.zalando.problem.spring.web.advice.routing.RoutingAdviceTrait;
import org.zalando.problem.spring.web.advice.validation.ValidationAdviceTrait;

/**
 * {@link ProblemHandling} is a composite {@link AdviceTrait} that combines all built-in advice traits into a single
 * interface that makes it easier to use:
 * <p>
 * <pre>{@code
 * &#064;ControllerAdvice
 * public class ExceptionHandling implements ProblemHandling
 * }</pre>
 * <p>
 * <strong>Note:</strong> Future versions of this class will be extended with additional traits.
 *
 * @see AdviceTrait
 * @see CustomAdviceTrait
 * @see GeneralAdviceTrait
 * @see HttpAdviceTrait
 * @see IOAdviceTrait
 * @see RoutingAdviceTrait
 * @see ValidationAdviceTrait
 */
public interface ProblemHandling extends
        GeneralAdviceTrait,
        HttpAdviceTrait,
        IOAdviceTrait,
        RoutingAdviceTrait,
        ValidationAdviceTrait {

}
