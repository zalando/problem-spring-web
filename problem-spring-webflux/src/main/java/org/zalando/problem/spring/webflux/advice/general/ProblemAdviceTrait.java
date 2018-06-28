package org.zalando.problem.spring.webflux.advice.general;

import org.apiguardian.api.API;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.webflux.advice.AdviceTrait;
import reactor.core.publisher.Mono;

import static org.apiguardian.api.API.Status.INTERNAL;
import static org.apiguardian.api.API.Status.STABLE;

/**
 * @see Problem
 * @see ThrowableProblem
 */
@API(status = STABLE)
public interface ProblemAdviceTrait extends AdviceTrait {

    @API(status = INTERNAL)
    @ExceptionHandler
    default Mono<ResponseEntity<Problem>> handleProblem(
            final ThrowableProblem problem,
            final ServerWebExchange request) {
        return create(problem, request);
    }

}
