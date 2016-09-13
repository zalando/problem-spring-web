package org.zalando.problem.spring.web.advice.general;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.AdviceTrait;

/**
 * @see Problem
 * @see ThrowableProblem
 */
public interface ProblemAdviceTrait extends AdviceTrait {

    @ExceptionHandler
    default ResponseEntity<Problem> handleProblem(
            final ThrowableProblem problem,
            final NativeWebRequest request) {
        return create(problem, request);
    }

}
