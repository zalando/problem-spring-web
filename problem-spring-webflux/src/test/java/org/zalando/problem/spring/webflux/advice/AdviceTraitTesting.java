package org.zalando.problem.spring.webflux.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.test.web.reactive.server.MockServerConfigurer;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.spring.webflux.advice.example.ExampleRestController;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

public interface AdviceTraitTesting {

    default ProblemHandling unit() {
        return new ExceptionHandling();
    }

    default WebTestClient webTestClient() {
        return WebTestClient.bindToController(new ExampleRestController())
                .apply(new MockServerConfigurer() {
                    @Override
                    public void beforeServerCreated(WebHttpHandlerBuilder builder) {
                        builder.exceptionHandlers(handlers ->
                                handlers.add(0, new ProblemExceptionHandler(mapper(), unit())));
                    }
                })
                .controllerAdvice(unit())
                .httpMessageCodecs(this::configureJson)
                .configureClient()
                .exchangeStrategies(ExchangeStrategies.builder().codecs(this::configureJson).build())
                .build();
    }

    default void configureJson(CodecConfigurer configurer) {
        final ObjectMapper mapper = mapper();
        CodecConfigurer.DefaultCodecs defaults = configurer.defaultCodecs();
        defaults.jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
        defaults.jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
    }

    default ObjectMapper mapper() {
        return new ObjectMapper()
                .registerModule(new ProblemModule())
                .registerModule(new ConstraintViolationProblemModule());
    }

}

