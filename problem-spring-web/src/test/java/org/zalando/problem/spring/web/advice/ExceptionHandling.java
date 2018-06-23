package org.zalando.problem.spring.web.advice;

import com.google.common.base.CaseFormat;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public final class ExceptionHandling implements ProblemHandling {

    @Override
    public String formatFieldName(final String fieldName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
    }

}
