package org.zalando.problem.spring.web.advice.general;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
class NestedProblemHandling implements ProblemHandling {

    @Override
    public boolean isCausalChainsEnabled() {
        return true;
    }

}
