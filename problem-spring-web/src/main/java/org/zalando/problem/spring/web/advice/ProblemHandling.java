package org.zalando.problem.spring.web.advice;

import org.apiguardian.api.API;
import org.zalando.problem.spring.web.advice.custom.CustomAdviceTrait;
import org.zalando.problem.spring.web.advice.general.GeneralAdviceTrait;
import org.zalando.problem.spring.web.advice.io.IOAdviceTrait;
import org.zalando.problem.spring.web.advice.network.NetworkAdviceTrait;
import org.zalando.problem.spring.web.advice.validation.ValidationAdviceTrait;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * {@link ProblemHandling} is a composite {@link AdviceTrait} that combines all built-in advice traits into a single
 * interface that makes it easier to use:
 * <pre><code>
 * {@literal @}ControllerAdvice
 *  public class ExceptionHandling implements ProblemHandling
 * </code></pre>
 * <strong>Note:</strong> Future versions of this class will be extended with additional traits.
 *
 * @see AdviceTrait
 * @see CustomAdviceTrait
 * @see GeneralAdviceTrait
 * @see IOAdviceTrait
 * @see ValidationAdviceTrait
 */
@API(status = STABLE)
public interface ProblemHandling extends
        GeneralAdviceTrait,
        IOAdviceTrait,
        NetworkAdviceTrait,
        ValidationAdviceTrait {

}
