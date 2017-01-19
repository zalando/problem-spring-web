package org.zalando.problem.validation;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.zalando.problem.spring.web.advice.validation.ConstraintViolationProblem;
import org.zalando.problem.spring.web.advice.validation.Violation;

/**
 * A companion to {@link org.zalando.problem.ProblemModule} to enable serialization
 * of {@link ConstraintViolationProblem} and {@link Violation} without relying on autodetection.
 */
public class ValidationProblemModule extends Module {
    @Override
    public void setupModule(SetupContext context) {
        final SimpleModule module = new SimpleModule();

        module.setMixInAnnotation(Violation.class, ViolationMixIn.class);
        module.setMixInAnnotation(ConstraintViolationProblem.class, ConstraintViolationProblemMixin.class);

        module.setupModule(context);
    }

    @Override
    public String getModuleName() {
        return ValidationProblemModule.class.getSimpleName();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Version version() {
        return VersionUtil.mavenVersionFor(ValidationProblemModule.class.getClassLoader(),
                "org.zalando", "problem-spring-web");
    }
}
