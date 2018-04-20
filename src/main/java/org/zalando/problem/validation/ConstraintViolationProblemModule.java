package org.zalando.problem.validation;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apiguardian.api.API;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;
import org.zalando.problem.spring.web.advice.validation.Violation;

import static org.apiguardian.api.API.Status.STABLE;

/**
 * A companion to {@link org.zalando.problem.ProblemModule} to enable serialization
 * of {@link ConstraintViolationProblem} and {@link Violation} without relying on auto detection.
 */
@API(status = STABLE)
public final class ConstraintViolationProblemModule extends Module {

    @Override
    public void setupModule(final SetupContext context) {
        final SimpleModule module = new SimpleModule()
                .setMixInAnnotation(ConstraintViolationProblem.class, ConstraintViolationProblemMixin.class)
                .setMixInAnnotation(Violation.class, ViolationMixIn.class);

        module.setupModule(context);
    }

    @Override
    public String getModuleName() {
        return ConstraintViolationProblemModule.class.getSimpleName();
    }

    @Override
    @SuppressWarnings("deprecation")
    public Version version() {
        return VersionUtil.mavenVersionFor(ConstraintViolationProblemModule.class.getClassLoader(),
                "org.zalando", "problem-spring-web");
    }

}
